package superstartrek.client.model;

import java.util.ArrayList;
import java.util.List;

import superstartrek.client.Application;
import superstartrek.client.activities.combat.FireHandler;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.navigation.EnterpriseRepairedEvent;
import superstartrek.client.activities.navigation.EnterpriseWarpedEvent;
import superstartrek.client.activities.navigation.ThingMovedEvent;
import superstartrek.client.control.GameOverEvent;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.TurnEndedEvent;
import superstartrek.client.control.TurnStartedEvent;
import superstartrek.client.utils.Random;

public class Enterprise extends Vessel implements GamePhaseHandler, FireHandler {

	public final static double PHASER_RANGE = 3;
	public final static double ANTIMATTER_CONSUMPTION_WARP = 10;
	public final static double IMPULSE_CONSUMPTION = 5;

	protected Setting phasers = new Setting("phasers", 30, 150);
	protected Setting torpedos = new Setting("torpedos", 10, 10);
	protected Setting antimatter = new Setting("antimatter", 1000, 1000);
	protected Setting reactor = new Setting("reactor", 60, 60);
	protected Setting autoAim = new Setting("auto aim", 1, 1);

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

	public Enterprise(Application app) {
		super(new Setting("impulse", 3, 3), new Setting("shields", 100, 100));
		setName("NCC 1701 USS Enterprise");
		setSymbol("O=Ξ");
		setCss("enterprise");
		app.events.addHandler(TurnStartedEvent.TYPE, this);
		app.events.addHandler(FireEvent.TYPE, this);
		app.events.addHandler(TurnEndedEvent.TYPE, this);
	}

	public Setting getAntimatter() {
		return antimatter;
	}

	public boolean warpTo(Quadrant destinationQuadrant, Runnable callbackBeforeWarping) {
		Location fromLocation = getLocation();
		Quadrant fromQuadrant = getQuadrant();
		if (!consume("warp", 20)) {
			// we can let this slide if no enemies in quadrant
			if (!getQuadrant().getKlingons().isEmpty()) {
				Application.get().message("Insufficient reactor output");
				return false;
			}
		}
		int destinationX = destinationQuadrant.getX();
		int destinationY = destinationQuadrant.getY();

		List<Quadrant> container = new ArrayList<Quadrant>();

		StarMap map = Application.get().starMap;

		map.walkLine(getQuadrant().getX(), getQuadrant().getY(), destinationX, destinationY, new Walker() {

			@Override
			public boolean visit(int x, int y) {
				Quadrant q = map.getQuadrant(x, y);
				List<Klingon> klingons = q.getKlingons();
				container.clear();
				container.add(q);
				// TODO for now, allow warping out of the departure quadrant
				if (!(x == getQuadrant().getX() && y == getQuadrant().getY()) && !klingons.isEmpty()) {
					Application.get().message("We were intercepted by " + klingons.get(0).getName(), "intercepted");
					return false;
				}
				return true;
			}
		});

		Quadrant dropQuadrant = container.get(0);
		setQuadrant(dropQuadrant);
		Location freeSpot = map.findFreeSpotAround(getQuadrant(), getLocation(), 3);
		Location oldLocation = getLocation();
		setLocation(freeSpot);
		int xFrom = Math.max(0, dropQuadrant.getX() - 1);
		int xTo = Math.min(7, dropQuadrant.getX() + 1);
		int yFrom = Math.max(0, dropQuadrant.getY() - 1);
		int yTo = Math.min(7, dropQuadrant.getY() + 1);
		for (int y = yFrom; y <= yTo; y++)
			for (int x = xFrom; x <= xTo; x++)
				map.getQuadrant(x, y).setExplored(true);
		if (callbackBeforeWarping!=null)
			callbackBeforeWarping.run();
		EnterpriseWarpedEvent warpEvent = new EnterpriseWarpedEvent(this, fromQuadrant, fromLocation, dropQuadrant,
				freeSpot);

		Application.get().events.fireEvent(warpEvent);
		ThingMovedEvent moveEvent = new ThingMovedEvent(this, warpEvent.qFrom, oldLocation, warpEvent.qTo, freeSpot);
		Application.get().events.fireEvent(moveEvent);
		return true;
	}
	
	public List<Location> getReachableSectors(){
		List<Location> list = new ArrayList<>();
		double range = getImpulse().getValue();
		while (range>0 && computeConsumptionForImpulseNavigation(range)>=getReactor().getValue())
			range = range-0.25;
		if (range <=0)
			return list;
		double range_squared = range*range;
		Location loc = getLocation();
		int minX = (int)Math.max(0,loc.getX() - range);
		int maxX = (int)Math.min(7,loc.getX() + range);
		int minY = (int)Math.max(0,loc.getY() - range);
		int maxY = (int)Math.min(7,loc.getY() + range);
		StarMap map = Application.get().starMap;
		for (int y=minY;y<=maxY;y++)
		for (int x=minX;x<=maxX;x++) {
			//squared distance check saves one sqrt() call and thus is faster
			if (StarMap.distance_squared(loc.getX(), loc.getY(), x, y)>range_squared)
				continue;
			Thing thing = map.findThingAt(getQuadrant(), x, y);
			if (thing != null && !Klingon.isCloakedKlingon(thing))
				continue; //TODO: cloaked klingons shouldn't count
			Location tmp = Location.location(x, y);
			List<Thing> obstacles = map.findObstaclesInLine(getQuadrant(), loc, tmp);
			if (obstacles.size()==1) list.add(tmp); else {
				if (Klingon.isCloakedKlingon(obstacles.get(1)))
					list.add(tmp);
			}
		}
		return list;
	}

	// only for internal use, bypasses checks
	public void _navigateTo(Location loc) {
		Location oldLoc = getLocation();
		this.setLocation(loc);
		Application.get().events.fireEvent(new ThingMovedEvent(this, getQuadrant(), oldLoc, getQuadrant(), loc));
	}

	public void navigateTo(Location loc) {
		Application app = Application.get();
		double distance = StarMap.distance(this.getLocation(), loc);
		if (distance > getImpulse().getValue()) {
			app.message("Course " + Math.round(distance) + " exceeds maximum impulse power " + getImpulse().getValue());
			return;
		}
		List<Location> path = new ArrayList<>();
		path.add(getLocation());
		app.starMap.walkLine(getLocation().getX(), getLocation().getY(), loc.getX(), loc.getY(), new Walker() {

			@Override
			public boolean visit(int x, int y) {
				Thing thing = app.starMap.findThingAt(getQuadrant(), x, y);
				if (thing != null && thing != app.starMap.enterprise) {
					if (thing instanceof Klingon && ((Klingon) thing).isCloaked()) {
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
		return distance*IMPULSE_CONSUMPTION;
	}

	public void fireTorpedosAt(Location sector) {
		if (!torpedos.isEnabled()) {
			Application.get().message("Torpedo bay is damaged");
			return;
		}
		if (torpedos.getValue() < 1) {
			Application.get().message("Torpedo bay is empty");
			return;
		}

		List<Thing> things = Application.get().starMap.findObstaclesInLine(quadrant, getLocation(), sector);
		things.remove(this);
		getTorpedos().decrease(1);
		Thing target=null;
		Random random = Application.get().random;
		for (Thing thing : things) {
			boolean hit = false;
			if (thing instanceof Star) {
				hit = true;
			} else if (thing instanceof StarBase) {
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

		FireEvent event = new FireEvent(FireEvent.Phase.fire, this, target, "torpedos", 50, false);
		Application.get().events.fireEvent(event);
		event = new FireEvent(FireEvent.Phase.afterFire, this, target, "torpedos", 50, false);
		Application.get().events.fireEvent(event);
		if (target == null)
			Application.get().message("Torpedo exploded in the void");
	}

	public void firePhasersAt(Location sector, boolean isAutoAim) {
		Thing thing = Application.get().starMap.findThingAt(quadrant, sector.getX(), sector.getY());
		if (thing == null) {
			Application.get().message("There is nothing at " + sector);
			return;
		}
		if (!(thing instanceof Klingon)) {
			Application.get().message("Phasers can target only enemy vessels");
			return;
		}
		double distance = StarMap.distance(this, thing);
		if (distance > PHASER_RANGE) {
			Application.get().message("Target is too far away.");
			return;
		}
		if (!phasers.isEnabled()) {
			if (!isAutoAim)
				Application.get().message("Phaser array is disabled");
			return;
		}
		if (phasers.getValue() == 0) {
			Application.get().message("Phasers already fired.");
			return;
		}
		if (!consume("phasers", phasers.getValue())) {
			if (!isAutoAim)
				Application.get().message("Insufficient reactor output");
			return;
		}
		Klingon klingon = (Klingon) thing;
		FireEvent event = new FireEvent(FireEvent.Phase.fire, this, klingon, "phasers", phasers.getValue() / distance, isAutoAim);
		Application.get().events.fireEvent(event);

		event = new FireEvent(FireEvent.Phase.afterFire, this, klingon, "phasers", phasers.getValue() / distance, isAutoAim);
		Application.get().events.fireEvent(event);

		phasers.setValue(0);
	}

	public void dockAtStarbase(StarBase starBase) {
		phasers.repair();
		torpedos.repair();
		impulse.repair();
		shields.repair();
		autoAim.repair();
		Application.get().events.fireEvent(new EnterpriseRepairedEvent(this));
	}

	protected boolean canBeRepaired(Setting setting) {
		return !setting.isEnabled() || setting.getCurrentUpperBound() < 0.75 * setting.getMaximum();
	}

	protected boolean maybeRepairProvisionally(Setting setting) {
		boolean needsRepair = canBeRepaired(setting);
		if (!needsRepair)
			return false;
		if (Application.get().random.nextDouble() < 0.5)
			return false;
		setting.setCurrentUpperBound(Math.max(1, setting.getMaximum() * 0.75)); // boolean settings can be repaired
																				// fully
		setting.setValue(setting.getDefaultValue());
		setting.setEnabled(true);
		Application.get().message("Repaired " + setting.getName());
		return true;
	}

	public void repairProvisionally() {
		int i = 10;
		while (i-- > 0) {
			boolean repaired = maybeRepairProvisionally(impulse) || maybeRepairProvisionally(shields)
					|| maybeRepairProvisionally(phasers) || maybeRepairProvisionally(torpedos)
					|| maybeRepairProvisionally(autoAim);
			if (repaired) {
				Application.get().events.fireEvent(new EnterpriseRepairedEvent(this));
				return;
			}
		}
		Application.get().message("Couldn't repair anything");
	}

	public boolean canRepairProvisionally() {
		return canBeRepaired(impulse) || canBeRepaired(shields) || canBeRepaired(phasers) || canBeRepaired(torpedos)
				|| canBeRepaired(autoAim);
	}

	public boolean isDamaged() {
		return impulse.getCurrentUpperBound() < impulse.getMaximum()
				|| shields.getCurrentUpperBound() < shields.getMaximum()
				|| phasers.getCurrentUpperBound() < phasers.getCurrentUpperBound() || !torpedos.isEnabled()
				|| !autoAim.isEnabled();
	}

	public void damageShields() {
		shields.damage(30);
		Application.get().message("Shields damaged, dropped to %" + shields.percentageHealth(), "enterprise-damaged");
	}

	public void damageImpulse() {
		impulse.damage(1);
		if (impulse.getValue() < 1)
			impulse.setEnabled(false);
		Application.get().message("Impulse drive damaged", "enterprise-damaged");
	}

	public void damageTorpedos() {
		torpedos.setEnabled(false);
		Application.get().message("Torpedo bay damaged", "enterprise-damaged");
	}

	public void damagePhasers() {
		phasers.damage(phasers.getMaximum() * 0.3);
		if (phasers.getCurrentUpperBound() < 1)
			phasers.setEnabled(false);
		Application.get().message("Phaser array damaged", "enterprise-damaged");
	}

	public void damageAutoaim() {
		autoAim.setEnabled(false);
		Application.get().message("Tactical computer damaged", "enterprise-damaged");
	}

	public void applyDamage(double damage) {
		double impact = 0.5 * damage / (shields.getValue() + 1.0);
		shields.decrease(damage);
		Random random = Application.get().random;
		if (shields.getCurrentUpperBound() > 0 && 0.7 * random.nextDouble() < impact)
			damageShields();
		if (impulse.getCurrentUpperBound() > 0 && random.nextDouble() < impact)
			damageImpulse();
		if (torpedos.isEnabled() && random.nextDouble() < impact)
			damageTorpedos();
		if (phasers.getCurrentUpperBound() > 0 && random.nextDouble() < impact)
			damagePhasers();
		if (autoAim.isEnabled() && random.nextDouble() * 1.2 < impact)
			damageAutoaim();
	}

	public boolean consume(String what, double value) {
		if (getReactor().getValue() < value)
			return false;
		getReactor().decrease(value);
		return true;
	}

	public double computeEnergyConsumption() {
		return getShields().getValue() / 10 + 10.0;
	}

	public void autoAim() {
		for (Klingon k : getQuadrant().getKlingons())
			if (!k.isCloaked() && StarMap.within_distance(this, k, PHASER_RANGE)) {
				firePhasersAt(k.getLocation(), true);
				return;
			}
	}

	public void playComputerTurn() {
		if (getAutoAim().getBooleanValue() && getAutoAim().isEnabled())
			autoAim();
	}

	@Override
	public void onTurnStarted(TurnStartedEvent evt) {
		phasers.reset();
		reactor.reset();
		shields.reset();
		impulse.reset();
		if (!consume("energy", computeEnergyConsumption())) {
			Application.get().events.fireEvent(new GameOverEvent(GameOverEvent.Outcome.lost, "Out of energy"));
			return;
		}
		playComputerTurn();
	}

	public void toggleAutoAim() {
		getAutoAim().setValue(!getAutoAim().getBooleanValue() && getAutoAim().isEnabled());
	}

	@Override
	public void onFire(FireEvent evt) {
		if (evt.target != this)
			return;
		Application.get().message(evt.actor.getName() + " at " + evt.actor.getLocation() + " fired on us", "damage");
		applyDamage(evt.damage);
	}

}
