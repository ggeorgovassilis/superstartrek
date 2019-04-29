package superstartrek.client.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;

import superstartrek.client.Application;
import superstartrek.client.activities.combat.FireHandler;
import superstartrek.client.activities.computer.EnergyConsumptionHandler;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.navigation.EnterpriseWarpedHandler.EnterpriseWarpedEvent;
import superstartrek.client.activities.navigation.ThingMovedHandler.ThingMovedEvent;
import superstartrek.client.control.AfterTurnStartedEvent;
import superstartrek.client.control.GameOverEvent;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.TurnEndedEvent;
import superstartrek.client.control.TurnStartedEvent;
import superstartrek.client.utils.Random;
import superstartrek.client.activities.navigation.EnterpriseRepairedHandler.EnterpriseRepairedEvent;
import superstartrek.client.activities.navigation.EnterpriseDamagedHandler.EnterpriseDamagedEvent;

public class Enterprise extends Vessel implements GamePhaseHandler, FireHandler {

	public final static double PHASER_RANGE = 3;
	public final static double ANTIMATTER_CONSUMPTION_WARP = 20;
	public final static double IMPULSE_CONSUMPTION = 5;

	Application application;
	StarMap starMap;
	Setting phasers = new Setting("phasers", 30, 150);
	Setting torpedos = new Setting("torpedos", 10, 10);
	Setting antimatter = new Setting("antimatter", 1000, 1000);
	Setting reactor = new Setting("reactor", 60, 60);
	Setting autoAim = new Setting("auto aim", 1, 1);
	Setting lrs = new Setting("LRS",1,1);
	Quadrant quadrant;
	
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
		super(new Setting("impulse", 3, 3), new Setting("shields", 100, 100));
		this.application = app;
		this.starMap = map;
		setName("NCC 1701 USS Enterprise");
		setSymbol("O=Ξ");
		setCss("enterprise");
		application.events.addHandler(TurnStartedEvent.TYPE, this);
		application.events.addHandler(FireEvent.TYPE, this);
		application.events.addHandler(TurnEndedEvent.TYPE, this);
	}

	public Setting getAntimatter() {
		return antimatter;
	}

	public boolean warpTo(Quadrant destinationQuadrant, Runnable callbackBeforeWarping) {
		Location fromLocation = getLocation();
		Quadrant fromQuadrant = getQuadrant();
		if (!consume("warp", 20) && !getQuadrant().getKlingons().isEmpty()) {
			// we can let this slide if no enemies in quadrant
			application.message("Insufficient reactor output");
			return false;
		}
		int destinationX = destinationQuadrant.getX();
		int destinationY = destinationQuadrant.getY();

		List<Quadrant> container = new ArrayList<Quadrant>();

		starMap.walkLine(getQuadrant().getX(), getQuadrant().getY(), destinationX, destinationY, new Walker() {

			@Override
			public boolean visit(int x, int y) {
				Quadrant q = starMap.getQuadrant(x, y);
				List<Klingon> klingons = q.getKlingons();
				container.clear();
				container.add(q);
				// TODO for now, allow warping out of the departure quadrant
				if (!(x == getQuadrant().getX() && y == getQuadrant().getY()) && !klingons.isEmpty()) {
					application.message("We were intercepted by " + klingons.get(0).getName(), "intercepted");
					return false;
				}
				return true;
			}
		});

		Quadrant dropQuadrant = container.get(0);
		setQuadrant(dropQuadrant);
		Location freeSpot = starMap.findFreeSpotAround(getQuadrant(), getLocation());
		Location oldLocation = getLocation();
		setLocation(freeSpot);
		int xFrom = Math.max(0, dropQuadrant.getX() - 1);
		int xTo = Math.min(7, dropQuadrant.getX() + 1);
		int yFrom = Math.max(0, dropQuadrant.getY() - 1);
		int yTo = Math.min(7, dropQuadrant.getY() + 1);
		for (int y = yFrom; y <= yTo; y++)
			for (int x = xFrom; x <= xTo; x++)
				starMap.getQuadrant(x, y).setExplored(true);
		if (callbackBeforeWarping != null)
			callbackBeforeWarping.run();
		consume("Warp", ANTIMATTER_CONSUMPTION_WARP*StarMap.distance(fromQuadrant.x, fromQuadrant.y, dropQuadrant.x, dropQuadrant.y));
		EnterpriseWarpedEvent warpEvent = new EnterpriseWarpedEvent(this, fromQuadrant, fromLocation, dropQuadrant,
				freeSpot);

		application.events.fireEvent(warpEvent);
		ThingMovedEvent moveEvent = new ThingMovedEvent(this, warpEvent.qFrom, oldLocation, warpEvent.qTo, freeSpot);
		application.events.fireEvent(moveEvent);
		turnsSinceWarp = 0;
		return true;
	}

	public List<Location> findReachableSectors() {
		List<Location> reachableSectors = new ArrayList<>();
		double range = getImpulse().getValue();
		while (range > 1 && computeConsumptionForImpulseNavigation(range) >= getReactor().getValue())
			range = range - 0.5;
		if (range < 1)
			return reachableSectors;
		double range_squared = range * range;
		int lx = getLocation().getX();
		int ly = getLocation().getY();
		int minX = (int) Math.max(0, lx - range);
		int maxX = (int) Math.min(7, lx + range);
		int minY = (int) Math.max(0, ly - range);
		int maxY = (int) Math.min(7, ly + range);
		StarMap map = application.starMap;
		QuadrantIndex index = new QuadrantIndex(getQuadrant(), map);
		for (int x = minX; x <= maxX; x++)
			for (int y = minY; y <= maxY; y++) {
				// squared distance check saves one sqrt() call and thus is faster
				if (StarMap.distance_squared(lx, ly, x, y) > range_squared)
					continue;
				Location tmp = Location.location(x, y);
				//TODO: isViewClear traces a trajectory from here to the tmp location. As we do this for every sector
				//in the disk, most sectors are visited multiple times. we need a different algorithm.
				if (isViewClear(index, tmp))
					reachableSectors.add(tmp);
			}
		return reachableSectors;
	}

	// only for internal use, bypasses checks
	public void _navigateTo(Location loc) {
		Location oldLoc = getLocation();
		this.setLocation(loc);
		application.events.fireEvent(new ThingMovedEvent(this, getQuadrant(), oldLoc, getQuadrant(), loc));
	}

	public void navigateTo(Location loc) {
		Application app = application;
		StarMap map = app.starMap;
		QuadrantIndex index = new QuadrantIndex(quadrant, map);
		if (!canNavigateTo(index, loc)) {
			// TODO: this should never be the case; navigation constraints are already
			// checked
			application.message("Can't go there");
			return;
		}
		double distance = StarMap.distance(this.getLocation(), loc);
		List<Location> path = new ArrayList<>();
		path.add(getLocation());
		map.walkLine(getLocation().getX(), getLocation().getY(), loc.getX(), loc.getY(), new Walker() {

			@Override
			public boolean visit(int x, int y) {
				Thing thing = index.findThingAt(x, y);
				if (thing != null && thing != app.starMap.enterprise) {
					if (Klingon.isCloakedKlingon(thing)) {
						((Klingon) thing).uncloak();
					}
					return false;
				}
				path.add(Location.location(x, y));
				return true;
			}
		});
		Location drop = path.get(path.size() - 1);
		if (!consume("impulse", computeConsumptionForImpulseNavigation(distance))) {
			app.message("Insufficient reactor output");
			return;
		}

		impulse.decrease(distance);
		_navigateTo(drop);
	}

	public double computeConsumptionForImpulseNavigation(double distance) {
		return distance * IMPULSE_CONSUMPTION;
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

		List<Thing> things = application.starMap.findObstaclesInLine(quadrant, getLocation(), sector, 8);
		things.remove(this);
		getTorpedos().decrease(1);
		Thing target = null;
		Random random = application.random;
		double damage = 50;
		for (Thing thing : things) {
			boolean hit = false;
			if (thing instanceof Star || thing instanceof StarBase) {
				hit = true;
			} else if (thing instanceof Klingon) {
				double distance = StarMap.distance(this, thing);
				double chance = Math.sqrt(2) / distance;
				hit = random.nextDouble() <= chance;
			}
			if (hit) {
				target = thing;
				break;
			}
		}
		if (target instanceof Klingon) {
			double shields = ((Klingon)target).getShields().getValue();
			double maxShields = ((Klingon)target).getShields().getMaximum();
			damage = damage*(1.0-(0.5*(shields/maxShields)*(shields/maxShields)));
		}
		FireEvent event = new FireEvent(FireEvent.Phase.fire, getQuadrant(), this, target, "torpedos", damage, false);
		application.events.fireEvent(event);
		event = new FireEvent(FireEvent.Phase.afterFire, getQuadrant(), this, target, "torpedos", damage, false);
		application.events.fireEvent(event);
		if (target == null)
			application.message("Torpedo exploded in the void");
	}

	public String canFirePhaserAt(Location sector) {
		Thing thing = application.starMap.findThingAt(quadrant, sector);
		if (thing == null || !thing.isVisible()) {
			return "There is nothing at " + sector;
		}
		if (!(thing instanceof Klingon)) {
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
		Thing thing = application.starMap.findThingAt(quadrant, sector);
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
		FireEvent event = new FireEvent(FireEvent.Phase.fire, getQuadrant(), this, klingon, "phasers", damage, isAutoAim);
		application.events.fireEvent(event);

		event = new FireEvent(FireEvent.Phase.afterFire, getQuadrant(), this, klingon, "phasers", damage, isAutoAim);
		application.events.fireEvent(event);

	}

	public void dockAtStarbase(StarBase starBase) {
		phasers.repair();
		torpedos.repair();
		impulse.repair();
		shields.repair();
		autoAim.repair();
		antimatter.repair();
		lrs.repair();
		application.events.fireEvent(new EnterpriseRepairedEvent(this));
	}
	
	protected boolean isViewClear(QuadrantIndex index, Location destination) {
		StarMap map = application.starMap;
		List<Thing> obstacles = map.findObstaclesInLine(index, getLocation(), destination, 8);
		obstacles.remove(this);
		boolean viewIsClear = true;
		for (int i=0;i<obstacles.size() && viewIsClear;i++)
			viewIsClear &= !obstacles.get(i).isVisible();
		return viewIsClear;
		
	}

	public boolean canNavigateTo(QuadrantIndex index, Location destination) {
		if (!getImpulse().isEnabled())
			return false;
		double distance = StarMap.distance(getLocation(), destination);
		if (distance > getImpulse().getValue())
			return false;
		if (getReactor().getValue() < computeConsumptionForImpulseNavigation(distance))
			return false;
		Thing thing = index.findThingAt(destination.getX(), destination.getY());
		if (!Klingon.isEmptyOrCloakedKlingon(thing))
			return false;
		return isViewClear(index, destination);
	}

	protected boolean canBeRepaired(Setting setting) {
		return !setting.isEnabled() || setting.getCurrentUpperBound() < 0.75 * setting.getMaximum();
	}

	protected boolean maybeRepairProvisionally(Setting setting) {
		boolean needsRepair = canBeRepaired(setting);
		if (!needsRepair)
			return false;
		if (application.random.nextDouble() < 0.5)
			return false;
		setting.setCurrentUpperBound(Math.max(1, setting.getMaximum() * 0.75)); // boolean settings can be repaired
																				// fully
		setting.setValue(setting.getDefaultValue());
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
				application.events.fireEvent(new EnterpriseRepairedEvent(this));
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
		//from a game-play POV being damaged right after jumping into a quadrant sucks, that's why the damage is reduced in this case.
		//the in-world justification is that opponents can't get a reliable target lock
		if (turnsSinceWarp<2) {
			damage = damage*0.5;
			GWT.log("Warp damage protection applies");
		}
		shields.decrease(damage);
		Random random = application.random;
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
		application.events.fireEvent(new EnterpriseDamagedEvent(this));
	}

	public boolean consume(String what, double value) {
		if (getReactor().getValue() < value)
			return false;
		getReactor().decrease(value);
		getAntimatter().decrease(value);
		application.events.fireEvent(new EnergyConsumptionHandler.EnergyConsumptionEvent(this, value, what));
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
	public void onTurnStarted(TurnStartedEvent evt) {
		phasers.reset();
		reactor.reset();
		shields.reset();
		impulse.reset();
		if (!consume("energy", computeEnergyConsumption())) {
			application.events.fireEvent(new GameOverEvent(GameOverEvent.Outcome.lost, "Out of energy"));
			return;
		}
		playComputerTurn();
	}

	public void toggleAutoAim() {
		autoAim.setValue(!getAutoAim().getBooleanValue() && getAutoAim().isEnabled());
	}

	@Override
	public void onFire(FireEvent evt) {
		if (evt.target != this)
			return;
		application.message(evt.actor.getName() + " at " + evt.actor.getLocation() + " fired on us", "damage");
		applyDamage(evt.damage);
	}
	
	@Override
	public void onTurnEnded(TurnEndedEvent evt) {
		turnsSinceWarp++;
	}

}
