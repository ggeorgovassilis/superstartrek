package superstartrek.client.model;

import java.util.ArrayList;
import java.util.List;

import superstartrek.client.Application;
import superstartrek.client.activities.combat.CombatHandler;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.bus.Events;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.utils.BaseMixin;
import superstartrek.client.utils.BrowserAPI;

public class Enterprise extends Vessel implements GamePhaseHandler, CombatHandler, BaseMixin {

	public final static double PHASER_RANGE = 3;
	public final static double ANTIMATTER_CONSUMPTION_WARP = 2;
	public final static double IMPULSE_CONSUMPTION = 2;
	public final static double DEVICE_IMPACT_MODIFIER = 0.3;
	public final static double SHIELD_IMPACT_MODIFIER = 0.5;
	public final static double CHANCE_OF_AUTOREPAIR = 0.3;
	public final static int TIME_TO_REPAIR_SETTING = 3;
	public final static double PRECISION_SHOT_EFFICIENCY=0.2;
	public final static double PHASER_EFFICIENCY=0.4;

	Setting phasers = new Setting(30);
	Setting torpedos = new Setting(10);
	Setting antimatter = new Setting(1000);
	Setting reactor = new Setting(50);
	Setting autoAim = new Setting(1);
	Setting lrs = new Setting(1);
	Setting warpDrive = new Setting(1);

	Application application;
	StarMap starMap;
	Quadrant quadrant;
	List<Location> reachableSectors = new ArrayList<>();

	int turnsSinceWarp = 0;

	public Setting getLrs() {
		return lrs;
	}

	public Quadrant getQuadrant() {
		return quadrant;
	}

	public void setQuadrant(Quadrant quadrant) {
		this.quadrant = quadrant;
	}

	public Setting getReactor() {
		return reactor;
	}

	public Setting getPhasers() {
		return phasers;
	}

	public Setting getTorpedos() {
		return torpedos;
	}

	public Setting getAutoAim() {
		return autoAim;
	}

	public Setting getWarpDrive() {
		return warpDrive;
	}

	public Enterprise(Application app, StarMap map) {
		super(new Setting(3), new Setting(100));
		this.application = app;
		this.starMap = map;
		setName("NCC 1701 USS Enterprise");
		setSymbol("O=Ξ");
		setCss("enterprise");
		addHandler(Events.TURN_STARTED, this);
		addHandler(Events.TURN_ENDED, this);
		addHandler(Events.GAME_RESTART, this);
		addHandler(Events.BEFORE_FIRE, this);
	}

	public Setting getAntimatter() {
		return antimatter;
	}

	public boolean warpTo(Quadrant destinationQuadrant, Runnable callbackBeforeWarping) {
		if (!warpDrive.isOperational()) {
			application.message("Warp drive is offline.", "info");
			return false;
		}
		final Location fromLocation = getLocation();
		final Quadrant fromQuadrant = getQuadrant();
		double necessaryEnergy = computeConsumptionForWarp(fromQuadrant, destinationQuadrant);
		if (!consume("warp", necessaryEnergy)) {
			// we can let this slide if no enemies in quadrant
			application.message("Insufficient reactor output");
			return false;
		}

		reachableSectors.clear();
		
		Quadrant[] container = new Quadrant[1];
		StarMap.walkLine(getQuadrant().x, getQuadrant().y, destinationQuadrant.x,
				destinationQuadrant.y, (x, y) -> {
					Quadrant q = starMap.getQuadrant(x, y);
					container[0] = q;
					List<Klingon> klingons = q.getKlingons();
					// TODO for now, allow warping out of the departure quadrant
					if (!(x == getQuadrant().x && y == getQuadrant().y) && !klingons.isEmpty()) {
						application.message("We were intercepted by " + klingons.get(0).getName(), "intercepted");
						return false;
					}
					return true;
				});

		if (callbackBeforeWarping != null)
			callbackBeforeWarping.run();

		Quadrant dropQuadrant = container[0];
		setQuadrant(dropQuadrant);
		application.starMap.markAsExploredAround(dropQuadrant);
		fireEvent(Events.QUADRANT_ACTIVATED, (h) -> h.onActiveQuadrantChanged(fromQuadrant, dropQuadrant));

		Location freeSpot = starMap.findFreeSpotAround(getQuadrant(), getLocation(),
				Constants.SECTORS_EDGE);
		Location oldLocation = getLocation();
		setLocation(freeSpot);

		fromQuadrant.removeEnterprise(this);
		fireEvent(Events.AFTER_ENTERPRISE_WARPED,
				(h) -> h.onEnterpriseWarped(this, fromQuadrant, fromLocation, dropQuadrant, freeSpot));
		dropQuadrant.addEnterprise(this);
		fireEvent(Events.THING_MOVED,
				(h) -> h.thingMoved(Enterprise.this, fromQuadrant, oldLocation, dropQuadrant, freeSpot));
		turnsSinceWarp = 0;
		return true;
	}

	public List<Location> getLastReachableSectors() {
		return reachableSectors;
	}

	public List<Location> findReachableSectors() {
		reachableSectors.clear();
		double range = computeImpulseNavigationRange();
		if (range < 1)
			return reachableSectors;
		double range_squared = range * range;
		int lx = getLocation().x;
		int ly = getLocation().y;
		int minX = (int) Math.max(0, lx - range);
		int maxX = (int) Math.min(Constants.SECTORS_EDGE - 1, lx + range);
		int minY = (int) Math.max(0, ly - range);
		int maxY = (int) Math.min(Constants.SECTORS_EDGE - 1, ly + range);
		final int NOT_REACHABLE = -1;
		final int REACHABLE = 1;
		final int UNKNOWN = 0;
		int[][] visitLog = new int[Constants.SECTORS_EDGE][Constants.SECTORS_EDGE]; // -1= not reachable, 0 = not
																					// visited yet, 1 = reachable
		visitLog[lx][ly] = 1;
		for (int x = minX; x <= maxX; x++)
			for (int y = minY; y <= maxY; y++) {
				if (visitLog[x][y] != UNKNOWN)
					continue;
				// squared distance check saves one sqrt() call and thus is faster
				if (StarMap.distance_squared(lx, ly, x, y) > range_squared)
					continue;
				StarMap.walkLine(lx, ly, x, y, (x1, y1) -> {
					if (visitLog[x1][y1] != UNKNOWN)
						return visitLog[x1][y1] == REACHABLE;
					// from here on, visitLog is known to be 0
					if (Thing.isVisible(getQuadrant().findThingAt(x1, y1))) {
						visitLog[x1][y1] = NOT_REACHABLE;
						return false;
					}
					visitLog[x1][y1] = REACHABLE;
					reachableSectors.add(Location.location(x1, y1));
					return true;
				});
			}
		return reachableSectors;
	}

	// only for internal use, bypasses checks
	void moveToIgnoringConstraints(Location loc) {
		Location oldLoc = getLocation();
		setLocation(loc);
		fireEvent(Events.THING_MOVED, (h) -> h.thingMoved(Enterprise.this, getQuadrant(), oldLoc, getQuadrant(), loc));
	}

	public void navigateTo(Location loc) {
		if (!canNavigateTo(loc)) {
			// TODO: this should never be the case; navigation constraints are already
			// checked. This assumes that cloaked klingons are "reachable"
			application.message("Can't go there");
			return;
		}
		double distance = StarMap.distance(this.getLocation(), loc);
		Location[] trace = new Location[] { getLocation() };
		StarMap.walkLine(getLocation().x, getLocation().y, loc.x, loc.y, (x, y) -> {
			Thing thing = quadrant.findThingAt(x, y);
			if (thing != null && thing != Enterprise.this) {
				if (Klingon.isCloakedKlingon(thing))
					((Klingon) thing).uncloak();
				return false;
			}
			trace[0] = Location.location(x, y);
			return true;
		});
		Location drop = trace[0];
		double energyNeededForMovement = computeConsumptionForImpulseNavigation(distance);
		if (!consume("impulse", energyNeededForMovement)) {
			message("Insufficient reactor output");
			return;
		}

		impulse.decrease(distance);
		moveToIgnoringConstraints(drop);
	}

	public void maybeAutoRepair() {
			repairProvisionally();
	}

	public double computeConsumptionForImpulseNavigation(double distance) {
		return distance*distance * IMPULSE_CONSUMPTION;
	}
	
	public double computeImpulseNavigationRange() {
		//The sqrt models more accurately the physics of E=0.5*m*v²
		//Also, a game-play-friendly side effect is that a minimum of maneuverability even
		//with a damaged reactor is possible.
		double vimp = impulse.isOperational()?impulse.getValue():0;
		return Math.min(vimp, Math.sqrt(getReactor().getValue()/IMPULSE_CONSUMPTION));
	}

	public double computeConsumptionForWarp(Quadrant from, Quadrant to) {
		return ANTIMATTER_CONSUMPTION_WARP * (5.0 + StarMap.distance_squared(from.x, from.y, to.x, to.y));
	}

	public void fireTorpedosAt(Location sector) {
		if (!torpedos.isOperational()) {
			application.message("Torpedo bay is damaged");
			return;
		}
		if (torpedos.getValue() < 1) {
			application.message("Torpedo bay is empty");
			return;
		}
		List<Thing> things = StarMap.findObstaclesInLine(getQuadrant(), getLocation(), sector, Constants.SECTORS_EDGE);
		things.remove(this);
		getTorpedos().decrease(1);
		Thing target = null;
		double damage = 50;
		BrowserAPI browser = application.browserAPI;
		double precision = 2.0 * (autoAim.isOperational() ? 1 : 0.7);
		for (Thing thing : things) {
			boolean hit = false;
			if (Klingon.is(thing)) {
				double distance = StarMap.distance(this, thing);
				double chance = precision / distance;
				hit = browser.nextDouble() <= chance;
			} else {
				hit = true;
			}
			if (hit) {
				target = thing;
				break;
			}
		}
		if (Klingon.is(target)) {
			double shields = ((Klingon) target).getShields().getValue();
			double maxShields = ((Klingon) target).getShields().getMaximum();
			damage = damage * (1.0 - (0.5 * (shields / maxShields) * (shields / maxShields)));
		}
		Thing eventTarget = target;
		double eventDamage = damage;
		fireEvent(Events.BEFORE_FIRE,
				(h) -> h.onFire(quadrant, Enterprise.this, eventTarget, Weapon.torpedo, eventDamage, false, partTarget.none));
		fireEvent(Events.AFTER_FIRE,
				(h) -> h.afterFire(quadrant, Enterprise.this, eventTarget, Weapon.torpedo, eventDamage, false));
		if (target == null)
			application.message("Torpedo exploded in the void");
	}

	public String canFirePhaserAt(Location sector) {
		Thing thing = quadrant.findThingAt(sector);
		if (thing == null || !thing.isVisible()) {
			return "There is nothing at " + sector;
		}
		if (!Klingon.is(thing)) {
			return "Phasers can target only enemy vessels";
		}
		double distance = StarMap.distance(this, thing);
		if (distance > PHASER_RANGE) {
			return "Target is too far away.";
		}
		if (!phasers.isOperational()) {
			return "Phaser banks are disabled";
		}
		if (phasers.getValue() == 0) {
			return "Phasers already fired.";
		}
		double phaserEnergy = Math.min(phasers.getValue(), reactor.getValue());
		if (phaserEnergy < 1) {
			return "Insufficient reactor output";
		}
		return null;
	}

	public void firePhasersAt(Location sector, boolean isAutoAim, partTarget precisionShot) {
		Thing thing = quadrant.findThingAt(sector);
		String error = canFirePhaserAt(sector);
		if (error != null) {
			if (!isAutoAim)
				application.message(error);
			return;
		}
		boolean isPrecisionShot = precisionShot!=partTarget.none;
		double phaserEnergy = Math.min(phasers.getValue(), reactor.getValue());
		if (phaserEnergy < 1)
			return;
		if (!consume("phasers", phaserEnergy)) {
			if (!isAutoAim)
				application.message("Insufficient reactor output");
			return;
		}
		Klingon klingon = (Klingon) thing;
		double distance = StarMap.distance(this, thing);
		double efficiency = isPrecisionShot?PRECISION_SHOT_EFFICIENCY:PHASER_EFFICIENCY;
		double damage = efficiency * phaserEnergy / distance;
		phasers.setValue(phasers.getValue() - phaserEnergy);
		fireEvent(Events.BEFORE_FIRE,
				(h) -> h.onFire(getQuadrant(), Enterprise.this, klingon, Weapon.phaser, damage, isAutoAim, precisionShot));
		fireEvent(Events.AFTER_FIRE,
				(h) -> h.afterFire(getQuadrant(), Enterprise.this, klingon, Weapon.phaser, damage, isAutoAim));
	}

	public void dockInStarbase() {
		StarBase starBase = quadrant.getStarBase();
		if (starBase == null)
			return;
		boolean inRange = StarMap.within_distance(this, quadrant.getStarBase(), 1.1);
		boolean hasKlingons = !quadrant.getKlingons().isEmpty();
		if (!inRange && hasKlingons) {
			application.message("Navigate manually when enemies present.");
			return;
		}
		if (!inRange && !hasKlingons) {
			Location loc = starMap.findFreeSpotAround(quadrant,
					quadrant.getStarBase().getLocation(), 2);
			if (loc == null) {
				application.message("No space around starbase");
				return;
			}
			this.moveToIgnoringConstraints(loc);
		}

		int repairCount = 0;
		repairCount += phasers.repair() ? 1 : 0;
		int torpedosRestocked = (int) (torpedos.getMaximum() - torpedos.getValue());
		repairCount += torpedos.repair() ? 1 : 0;
		repairCount += impulse.repair() ? 1 : 0;
		repairCount += shields.repair() ? 1 : 0;
		repairCount += autoAim.repair() ? 1 : 0;
		int antimatterRefuelled = (int) (antimatter.getMaximum() - antimatter.getValue());
		repairCount += antimatter.repair() ? 1 : 0;
		repairCount += lrs.repair() ? 1 : 0;
		repairCount += reactor.repair() ? 1 : 0;
		final int fRepairCount = repairCount;
		fireEvent(Events.ENTERPRISE_REPAIRED, (h) -> h.onEnterpriseRepaired(Enterprise.this));
		fireEvent(Events.ENTERPRISE_DOCKED, (h) -> h.onEnterpriseDocked(Enterprise.this, starBase, fRepairCount,
				torpedosRestocked, antimatterRefuelled));
		application.message("Enterprise docked at " + starBase.getName());
	}

	protected boolean canBeRepaired(Setting setting) {
		return setting.isBroken() || setting.getCurrentUpperBound() < 0.75 * setting.getMaximum();
	}

	protected boolean maybeRepairProvisionally(String name, Setting setting) {
		boolean needsRepair = canBeRepaired(setting);
		if (!needsRepair)
			return false;
		if (starMap.getStarDate() - setting.getTimeOfDamage() < TIME_TO_REPAIR_SETTING)
			return false;
		setting.setCurrentUpperBound(Math.max(1, setting.getMaximum() * 0.75)); // boolean settings can be repaired
																				// fully
		setting.setValue(setting.getCurrentUpperBound());
		setting.setBroken(false);
		application.message("Repaired " + name, "enterprise-repaired");
		return true;
	}

	public void repairProvisionally() {
		boolean v = maybeRepairProvisionally("impulse drive", impulse);
		v |= maybeRepairProvisionally("shields", shields);
		v |= maybeRepairProvisionally("phasers", phasers);
		v |= maybeRepairProvisionally("torpedo bay", torpedos);
		v |= maybeRepairProvisionally("tactical computer", autoAim);
		v |= maybeRepairProvisionally("LRS", lrs);
		v |= maybeRepairProvisionally("warp drive", warpDrive);
		v |= maybeRepairProvisionally("reactor", reactor);
		if (v) {
			fireEvent(Events.ENTERPRISE_REPAIRED, (h) -> h.onEnterpriseRepaired(Enterprise.this));
			return;
		}

	}

	public boolean canRepairProvisionally() {
		return canBeRepaired(impulse) || canBeRepaired(shields) || canBeRepaired(phasers) || canBeRepaired(torpedos)
				|| canBeRepaired(autoAim) || canBeRepaired(lrs) || canBeRepaired(warpDrive) || canBeRepaired(reactor);
	}

	public boolean isDamaged() {
		return impulse.getCurrentUpperBound() < impulse.getMaximum()
				|| shields.getCurrentUpperBound() < shields.getMaximum()
				|| phasers.getCurrentUpperBound() < phasers.getMaximum()
				|| reactor.getCurrentUpperBound() > reactor.getMaximum() || !torpedos.isOperational()
				|| !autoAim.isOperational() || !lrs.isOperational() || !warpDrive.isOperational();
	}

	public void damageShields() {
		shields.damage(30, starMap.getStarDate());
		application.message("Shields damaged, dropped to %" + shields.percentageHealth(), "enterprise-damaged");
	}

	public void damageImpulse() {
		impulse.damage(1, starMap.getStarDate());
		if (impulse.getValue() < 1)
			impulse.damageAndTurnOff(starMap.getStarDate());
		application.message("Impulse drive damaged", "enterprise-damaged");
	}

	public void damageTorpedos() {
		torpedos.damageAndTurnOff(starMap.getStarDate());
		torpedos.setValue(Math.floor(torpedos.getValue()*0.5));
		application.message("Torpedo bay damaged", "enterprise-damaged");
	}

	public void damagePhasers() {
		phasers.damage(phasers.getMaximum() * 0.3, starMap.getStarDate());
		if (phasers.getCurrentUpperBound() < 1)
			phasers.damageAndTurnOff(starMap.getStarDate());
		application.message("Phaser banks damaged", "enterprise-damaged");
	}

	public void damageReactor() {
		reactor.damage(reactor.getMaximum() * 0.3, starMap.getStarDate());
		if (reactor.getCurrentUpperBound() < 1)
			reactor.damageAndTurnOff(starMap.getStarDate());
		application.message("Reactor damaged", "enterprise-damaged");
	}

	public void damageAutoaim() {
		autoAim.damageAndTurnOff(starMap.getStarDate());
		application.message("Tactical computer damaged", "enterprise-damaged");
	}

	public void damageLRS() {
		lrs.damageAndTurnOff(starMap.getStarDate());
		application.message("LRS damaged", "enterprise-damaged");
	}

	public void damageWarpDrive() {
		warpDrive.damageAndTurnOff(starMap.getStarDate());
		application.message("Warp drive damaged", "enterprise-damaged");
	}

	public void applyDamage(double damage) {
		// from a game-play POV being damaged right after jumping into a quadrant sucks,
		// that's why the damage is reduced in this case.
		// the in-world justification is that opponents can't get a reliable target lock
		if (turnsSinceWarp < 2) {
			damage = damage * 0.5;
		}
		double shieldImpact = SHIELD_IMPACT_MODIFIER * damage / (shields.getValue() + 1.0);
		shields.decrease(shieldImpact);
		double deviceImpact = DEVICE_IMPACT_MODIFIER * damage / (shields.getValue() + 1.0);
		BrowserAPI random = application.browserAPI;
		if (shields.getCurrentUpperBound() > 0 && 0.7 * random.nextDouble() < deviceImpact)
			damageShields();
		if (impulse.getCurrentUpperBound() > 0 && random.nextDouble() < deviceImpact)
			damageImpulse();
		if (torpedos.isOperational() && random.nextDouble() < deviceImpact)
			damageTorpedos();
		if (phasers.getCurrentUpperBound() > 0 && random.nextDouble() < deviceImpact)
			damagePhasers();
		if (autoAim.isOperational() && random.nextDouble() < deviceImpact)
			damageAutoaim();
		if (reactor.isOperational() && random.nextDouble() < deviceImpact)
			damageReactor();
		if (lrs.isOperational() && random.nextDouble() < deviceImpact)
			damageLRS();
		if (warpDrive.isOperational() && random.nextDouble() < deviceImpact) {
			damageWarpDrive();
		}
		fireEvent(Events.ENTERPRISE_DAMAGED, (h) -> h.onEnterpriseDamaged(Enterprise.this));
	}

	public boolean consume(String what, double value) {
		if (getReactor().getValue() < value)
			return false;
		getReactor().decrease(value);
		//TODO: re-evaluate antimatter consumption for impulse movement. is this already covered with reactor consumption?
		getAntimatter().decrease(value);
		fireEvent(Events.CONSUME_ENERGY, (h) -> h.handleEnergyConsumption(this, value, what));
		return true;
	}

	public double computeEnergyConsumption() {
		return (getShields().getValue() + 1.0) * 0.08;
	}

	public void autoAim() {
		List<Klingon> potentialTargets = new ArrayList<Klingon>();
		for (Klingon k : getQuadrant().getKlingons())
			if (k.isVisible() && StarMap.within_distance(this, k, PHASER_RANGE)) {
				potentialTargets.add(k);
			}
		if (potentialTargets.isEmpty())
			return;
		Klingon target = potentialTargets.get(application.browserAPI.nextInt(potentialTargets.size()));
		firePhasersAt(target.getLocation(), true, partTarget.none);
		return;
	}

	public void playComputerTurn() {
		if (autoAim.getBooleanValue() && autoAim.isOperational())
			autoAim();
	}

	@Override
	public void onTurnStarted() {
		phasers.reset();
		reactor.reset();
		shields.reset();
		impulse.reset();
		if (!consume("energy", computeEnergyConsumption())) {
			fireEvent(Events.GAME_OVER, (h) -> h.gameLost());
			return;
		}
		maybeAutoRepair();
		playComputerTurn();
	}

	public void toggleAutoAim() {
		autoAim.setValue(!getAutoAim().getBooleanValue() && getAutoAim().isOperational());
	}

	@Override
	public void onFire(Quadrant quadrant, Vessel actor, Thing target, Weapon weapon, double damage,
			boolean wasAutoFire, partTarget part) {
		if (target != this)
			return;
		application.message(actor.getName() + " at " + actor.getLocation() + " fired on us", "damage");
		applyDamage(damage);
	}

	@Override
	public void onTurnEnded() {
		turnsSinceWarp++;
	}

	public static boolean is(Thing thing) {
		return thing instanceof Enterprise;
	}

	public boolean canNavigateTo(Location location) {
		return reachableSectors.contains(location);
	}

	@Override
	public void beforeGameRestart() {
		removeHandler(this);
		reachableSectors.clear();
	}

}
