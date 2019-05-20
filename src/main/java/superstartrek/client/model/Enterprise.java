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
	public final static double IMPULSE_CONSUMPTION = 5;

	Application application;
	StarMap starMap;
	Setting phasers = new Setting("phasers", 30);
	Setting torpedos = new Setting("torpedos", 10);
	Setting antimatter = new Setting("antimatter", 1000);
	Setting reactor = new Setting("reactor", 60);
	Setting autoAim = new Setting("auto aim", 1);
	Setting lrs = new Setting("LRS", 1);
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

	public Enterprise(Application app, StarMap map) {
		super(new Setting("impulse", 3), new Setting("shields", 100));
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
		Location fromLocation = getLocation();
		Quadrant fromQuadrant = getQuadrant();
		int destinationX = destinationQuadrant.getX();
		int destinationY = destinationQuadrant.getY();
		double necessaryEnergy = computeConsumptionForWarp(fromQuadrant, destinationQuadrant);
		// we always can warp out even if low on energy provided our sector is clean
		if (!consume("warp", necessaryEnergy) && !getQuadrant().getKlingons().isEmpty()) {
			// we can let this slide if no enemies in quadrant
			application.message("Insufficient reactor output");
			return false;
		}

		Quadrant[] container = new Quadrant[1];
		starMap.walkLine(getQuadrant().getX(), getQuadrant().getY(), destinationX, destinationY, (x, y) -> {
			Quadrant q = starMap.getQuadrant(x, y);
			container[0] = q;
			List<Klingon> klingons = q.getKlingons();
			// TODO for now, allow warping out of the departure quadrant
			if (!(x == getQuadrant().getX() && y == getQuadrant().getY()) && !klingons.isEmpty()) {
				application.message("We were intercepted by " + klingons.get(0).getName(), "intercepted");
				return false;
			}
			return true;
		});

		Quadrant dropQuadrant = container[0];
		setQuadrant(dropQuadrant);
		Location freeSpot = starMap.findFreeSpotAround(new QuadrantIndex(getQuadrant(), starMap), getLocation(), Constants.SECTORS_EDGE);
		Location oldLocation = getLocation();
		setLocation(freeSpot);
		starMap.markAsExploredAround(dropQuadrant);
		if (callbackBeforeWarping != null)
			callbackBeforeWarping.run();
		Quadrant qFrom = getQuadrant();
		fireEvent(Events.AFTER_ENTERPRISE_WARPED,
				(h) -> h.onEnterpriseWarped(this, fromQuadrant, fromLocation, dropQuadrant, freeSpot));

		fireEvent(Events.THING_MOVED, (h) -> h.thingMoved(Enterprise.this, qFrom, oldLocation, dropQuadrant, freeSpot));
		turnsSinceWarp = 0;
		return true;
	}

	public List<Location> findReachableSectors() {
		reachableSectors.clear();
		double range = getImpulse().getValue();
		while (range > 1 && computeConsumptionForImpulseNavigation(range) >= getReactor().getValue())
			range = range - 0.5;
		if (range < 1)
			return reachableSectors;
		double range_squared = range * range;
		int lx = getLocation().getX();
		int ly = getLocation().getY();
		int minX = (int) Math.max(0, lx - range);
		int maxX = (int) Math.min(Constants.SECTORS_EDGE-1, lx + range);
		int minY = (int) Math.max(0, ly - range);
		int maxY = (int) Math.min(Constants.SECTORS_EDGE-1, ly + range);
		QuadrantIndex index = new QuadrantIndex(getQuadrant(), starMap);
		final int NOT_REACHABLE = -1;
		final int REACHABLE = 1;
		final int UNKNOWN = 0;
		int[][] visitLog = new int[Constants.SECTORS_EDGE][Constants.SECTORS_EDGE]; // -1= not reachable, 0 = not visited yet, 1 = reachable
		visitLog[lx][ly] = 1;
		for (int x = minX; x <= maxX; x++)
			for (int y = minY; y <= maxY; y++) {
				if (visitLog[x][y] != UNKNOWN)
					continue;
				// squared distance check saves one sqrt() call and thus is faster
				if (StarMap.distance_squared(lx, ly, x, y) > range_squared)
					continue;
				starMap.walkLine(lx, ly, x, y, (x1, y1) -> {
					if (visitLog[x1][y1] != UNKNOWN)
						return visitLog[x1][y1] == REACHABLE;
					// from here on, visitLog is known to be 0
					if (Thing.isVisible(index.findThingAt(x1, y1))) {
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
	public void moveToIgnoringConstraints(Location loc) {
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
		QuadrantIndex index = new QuadrantIndex(quadrant, starMap);
		starMap.walkLine(getLocation().getX(), getLocation().getY(), loc.getX(), loc.getY(), (x, y) -> {
			Thing thing = index.findThingAt(x, y);
			if (thing != null && thing != Enterprise.this) {
				if (Klingon.isCloakedKlingon(thing))
					((Klingon) thing).uncloak();
				return false;
			}
			trace[0] = Location.location(x, y);
			return true;
		});
		Location drop = trace[0];
		if (!consume("impulse", computeConsumptionForImpulseNavigation(distance))) {
			message("Insufficient reactor output");
			return;
		}

		impulse.decrease(distance);
		moveToIgnoringConstraints(drop);
	}

	public double computeConsumptionForImpulseNavigation(double distance) {
		return distance * IMPULSE_CONSUMPTION;
	}

	public double computeConsumptionForWarp(Quadrant from, Quadrant to) {
		return ANTIMATTER_CONSUMPTION_WARP * (5.0 + StarMap.distance_squared(from.x, from.y, to.x, to.y));
	}

	public void fireTorpedosAt(Location sector) {
		if (!torpedos.isEnabled()) {
			application.message("Torpedo bay is damaged");
			return;
		}
		if (torpedos.getValue() < 1) {
			application.message("Torpedo bay is empty");
			return;
		}
		QuadrantIndex index = new QuadrantIndex(getQuadrant(), starMap);
		List<Thing> things = application.starMap.findObstaclesInLine(index, getLocation(), sector, Constants.SECTORS_EDGE);
		things.remove(this);
		getTorpedos().decrease(1);
		Thing target = null;
		double damage = 50;
		BrowserAPI browser = application.browserAPI;
		double precision = 2 * (autoAim.getBooleanValue() ? 1 : 0.7);
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
				(h) -> h.onFire(quadrant, Enterprise.this, eventTarget, "torpedos", eventDamage, false));
		fireEvent(Events.AFTER_FIRE,
				(h) -> h.afterFire(quadrant, Enterprise.this, eventTarget, "torpedos", eventDamage, false));
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
		if (!phasers.isEnabled()) {
			return "Phaser banks are disabled";
		}
		if (phasers.getValue() == 0) {
			return "Phasers already fired.";
		}
		if (getReactor().getValue() < phasers.getValue()) {
			return "Insufficient reactor output";
		}
		return null;
	}

	public void firePhasersAt(Location sector, boolean isAutoAim) {
		Thing thing = quadrant.findThingAt(sector);
		String error = canFirePhaserAt(sector);
		if (error != null) {
			if (!isAutoAim)
				application.message(error);
			return;
		}
		if (!consume("phasers", phasers.getValue())) {
			if (!isAutoAim)
				application.message("Insufficient reactor output");
			return;
		}
		Klingon klingon = (Klingon) thing;
		double distance = StarMap.distance(this, thing);
		double damage = phasers.getValue() / distance;
		phasers.setValue(0);
		fireEvent(Events.BEFORE_FIRE,
				(h) -> h.onFire(getQuadrant(), Enterprise.this, klingon, "phasers", damage, isAutoAim));
		fireEvent(Events.AFTER_FIRE,
				(h) -> h.afterFire(getQuadrant(), Enterprise.this, klingon, "phasers", damage, isAutoAim));
	}

	public void dockAtStarbase(StarBase starBase) {
		fireEvent(Events.ENTERPRISE_DOCKED, (h) -> h.onEnterpriseDocked(Enterprise.this, starBase));
		phasers.repair();
		torpedos.repair();
		impulse.repair();
		shields.repair();
		autoAim.repair();
		antimatter.repair();
		lrs.repair();
		fireEvent(Events.ENTERPRISE_REPAIRED, (h) -> h.onEnterpriseRepaired(Enterprise.this));
	}

	protected boolean canBeRepaired(Setting setting) {
		return !setting.isEnabled() || setting.getCurrentUpperBound() < 0.75 * setting.getMaximum();
	}

	protected boolean maybeRepairProvisionally(Setting setting) {
		boolean needsRepair = canBeRepaired(setting);
		if (!needsRepair)
			return false;
		if (application.browserAPI.nextDouble() < 0.5)
			return false;
		setting.setCurrentUpperBound(Math.max(1, setting.getMaximum() * 0.75)); // boolean settings can be repaired
																				// fully
		setting.setValue(setting.getCurrentUpperBound());
		setting.setEnabled(true);
		application.message("Repaired " + setting.getName());
		return true;
	}

	public void repairProvisionally() {
		int i = 10;
		while (i-- > 0) {
			boolean repaired = maybeRepairProvisionally(impulse) || maybeRepairProvisionally(shields)
					|| maybeRepairProvisionally(phasers) || maybeRepairProvisionally(torpedos)
					|| maybeRepairProvisionally(autoAim) || maybeRepairProvisionally(lrs);
			if (repaired) {
				fireEvent(Events.ENTERPRISE_REPAIRED, (h) -> h.onEnterpriseRepaired(Enterprise.this));
				return;
			}
		}
		application.message("Couldn't repair anything");
	}

	public boolean canRepairProvisionally() {
		return canBeRepaired(impulse) || canBeRepaired(shields) || canBeRepaired(phasers) || canBeRepaired(torpedos)
				|| canBeRepaired(autoAim) || canBeRepaired(lrs);
	}

	public boolean isDamaged() {
		return impulse.getCurrentUpperBound() < impulse.getMaximum()
				|| shields.getCurrentUpperBound() < shields.getMaximum()
				|| phasers.getCurrentUpperBound() < phasers.getMaximum() || !torpedos.isEnabled()
				|| !autoAim.isEnabled() || !lrs.isEnabled();
	}

	public void damageShields() {
		shields.damage(30);
		application.message("Shields damaged, dropped to %" + shields.percentageHealth(), "enterprise-damaged");
	}

	public void damageImpulse() {
		impulse.damage(1);
		if (impulse.getValue() < 1)
			impulse.setEnabled(false);
		application.message("Impulse drive damaged", "enterprise-damaged");
	}

	public void damageTorpedos() {
		torpedos.setEnabled(false);
		application.message("Torpedo bay damaged", "enterprise-damaged");
	}

	public void damagePhasers() {
		phasers.damage(phasers.getMaximum() * 0.3);
		if (phasers.getCurrentUpperBound() < 1)
			phasers.setEnabled(false);
		application.message("Phaser banks damaged", "enterprise-damaged");
	}

	public void damageAutoaim() {
		autoAim.setEnabled(false);
		application.message("Tactical computer damaged", "enterprise-damaged");
	}

	public void damageLRS() {
		lrs.setEnabled(false);
		application.message("LRS damaged", "enterprise-damaged");
	}

	public void applyDamage(double damage) {
		double impact = 0.5 * damage / (shields.getValue() + 1.0);
		// from a game-play POV being damaged right after jumping into a quadrant sucks,
		// that's why the damage is reduced in this case.
		// the in-world justification is that opponents can't get a reliable target lock
		if (turnsSinceWarp < 2) {
			damage = damage * 0.5;
		}
		shields.decrease(damage);
		BrowserAPI random = application.browserAPI;
		if (shields.getCurrentUpperBound() > 0 && 0.7 * random.nextDouble() < impact)
			damageShields();
		if (impulse.getCurrentUpperBound() > 0 && random.nextDouble() < impact)
			damageImpulse();
		if (torpedos.isEnabled() && random.nextDouble() < impact)
			damageTorpedos();
		if (phasers.getCurrentUpperBound() > 0 && random.nextDouble() < impact)
			damagePhasers();
		if (autoAim.isEnabled() && random.nextDouble() < impact)
			damageAutoaim();
		if (lrs.isEnabled() && random.nextDouble() < impact)
			damageLRS();
		fireEvent(Events.ENTERPRISE_DAMAGED, (h) -> h.onEnterpriseDamaged(Enterprise.this));
	}

	public boolean consume(String what, double value) {
		if (getReactor().getValue() < value)
			return false;
		getReactor().decrease(value);
		getAntimatter().decrease(value);
		fireEvent(Events.CONSUME_ENERGY, (h) -> h.handleEnergyConsumption(this, value, what));
		return true;
	}

	public double computeEnergyConsumption() {
		return getShields().getValue() / 10 + 10.0;
	}

	public void autoAim() {
		for (Klingon k : getQuadrant().getKlingons())
			if (k.isVisible() && StarMap.within_distance(this, k, PHASER_RANGE)) {
				firePhasersAt(k.getLocation(), true);
				return;
			}
	}

	public void playComputerTurn() {
		if (autoAim.getBooleanValue() && autoAim.isEnabled())
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
		playComputerTurn();
	}

	public void toggleAutoAim() {
		autoAim.setValue(!getAutoAim().getBooleanValue() && getAutoAim().isEnabled());
	}

	@Override
	public void onFire(Quadrant quadrant, Vessel actor, Thing target, String weapon, double damage,
			boolean wasAutoFire) {
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
	}

}
