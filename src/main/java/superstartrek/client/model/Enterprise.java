package superstartrek.client.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;

import superstartrek.client.Application;
import superstartrek.client.activities.combat.FireEvent;
import superstartrek.client.activities.combat.FireEvent.Phase;
import superstartrek.client.activities.combat.FireHandler;
import superstartrek.client.activities.computer.TurnEndedEvent;
import superstartrek.client.activities.computer.TurnEndedHandler;
import superstartrek.client.activities.computer.TurnStartedEvent;
import superstartrek.client.activities.computer.TurnStartedHandler;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.loading.GameOverEvent;
import superstartrek.client.activities.loading.GameOverEvent.Outcome;
import superstartrek.client.activities.navigation.EnterpriseRepairedEvent;
import superstartrek.client.activities.navigation.EnterpriseWarpedEvent;
import superstartrek.client.activities.navigation.ThingMovedEvent;
import superstartrek.client.utils.Random;

public class Enterprise extends Vessel implements TurnStartedHandler, FireHandler, TurnEndedHandler {

	public final static double PHASER_RANGE = 3;
	public final static double ANTIMATTER_CONSUMPTION_WARP = 10;
	public final static double IMPULSE_CONSUMPTION = 6;

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

	public boolean warpTo(Quadrant destinationQuadrant) {
		Location fromLocation = getLocation();
		Quadrant fromQuadrant = getQuadrant();
		if (destinationQuadrant == fromQuadrant)
			return false;
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
		int xFrom = Math.max(0, destinationX - 1);
		int xTo = Math.min(7, destinationX + 1);
		int yFrom = Math.max(0, destinationY - 1);
		int yTo = Math.min(7, destinationY + 1);
		for (int y = yFrom; y <= yTo; y++)
			for (int x = xFrom; x <= xTo; x++)
				map.getQuadrant(x, y).setExplored(true);

		EnterpriseWarpedEvent warpEvent = new EnterpriseWarpedEvent(this, fromQuadrant, fromLocation, dropQuadrant,
				freeSpot);

		Application.get().events.fireEvent(warpEvent);
		ThingMovedEvent moveEvent = new ThingMovedEvent(this, warpEvent.qFrom, oldLocation, warpEvent.qTo, freeSpot);
		Application.get().events.fireEvent(moveEvent);
		return true;
	}

	// only for internal use, bypasses checks
	public void _navigateTo(Location loc) {
		Location oldLoc = getLocation();
		this.setLocation(loc);
		Application.get().events.fireEvent(new ThingMovedEvent(this, getQuadrant(), oldLoc, getQuadrant(), loc));
	}

	public void navigateTo(Location loc) {
		Application app = Application.get();
		double distance = StarMap.distance(this, loc);
		if (distance > getImpulse().getValue()) {
			app.message("Course " + Math.round(distance) + " exceeds maximum impulse power " + getImpulse().getValue());
			return;
		}
		Thing thing = app.starMap.findThingAt(quadrant, loc.x, loc.y);
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
		if (!consume("impulse", distance * IMPULSE_CONSUMPTION)) {
			app.message("Insufficient reactor output");
			return;
		}

		impulse.decrease(distance);
		_navigateTo(drop);
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
		for (Thing thing : things) {
			boolean hit = false;
			if (thing instanceof Star) {
				hit = true;
			} else if (thing instanceof StarBase) {
				hit = true;
			} else if (thing instanceof Klingon) {
				double distance = StarMap.distance(this, thing);
				double chance = Math.sqrt(2) / distance;
				hit = Random.nextDouble() <= chance;
			}
			if (hit) {
				getTorpedos().decrease(1);
				FireEvent event = new FireEvent(Phase.fire, this, thing, "torpedos", 50);
				Application.get().events.fireEvent(event);
				event = new FireEvent(Phase.afterFire, this, thing, "torpedos", 50);
				Application.get().events.fireEvent(event);
				Application.get().endTurnAfterThis();
				return;
			}
		}
		Application.get().message("Torpedo exploded in the void");
		Application.get().endTurnAfterThis();
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
		FireEvent event = new FireEvent(FireEvent.Phase.fire, this, klingon, "phasers", phasers.getValue() / distance);
		Application.get().events.fireEvent(event);

		event = new FireEvent(FireEvent.Phase.afterFire, this, klingon, "phasers", phasers.getValue() / distance);
		Application.get().events.fireEvent(event);

		phasers.setValue(0);
		if (!isAutoAim)
			Application.get().endTurnAfterThis();
	}

	public void dockAtStarbase(StarBase starBase) {
		phasers.repair();
		torpedos.repair();
		impulse.repair();
		shields.repair();
		autoAim.repair();
		Application.get().events.fireEvent(new EnterpriseRepairedEvent());
		Application.get().endTurnAfterThis();
	}

	protected boolean canBeRepaired(Setting setting) {
		return !setting.isEnabled() || setting.getCurrentUpperBound() < 0.75 * setting.getMaximum();
	}

	protected boolean maybeRepairProvisionally(Setting setting) {
		boolean needsRepair = canBeRepaired(setting);
		if (!needsRepair)
			return false;
		if (superstartrek.client.utils.Random.nextDouble() < 0.5)
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
				Application.get().events.fireEvent(new EnterpriseRepairedEvent());
				Application.get().endTurnAfterThis();
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
		if (shields.getCurrentUpperBound() > 0 && 0.7 * Random.nextDouble() < impact)
			damageShields();
		if (impulse.getCurrentUpperBound() > 0 && Random.nextDouble() < impact)
			damageImpulse();
		if (torpedos.isEnabled() && Random.nextDouble() < impact)
			damageTorpedos();
		if (phasers.getCurrentUpperBound() > 0 && Random.nextDouble() < impact)
			damagePhasers();
		if (autoAim.isEnabled() && Random.nextDouble() * 1.2 < impact)
			damageAutoaim();
		if (shields.getValue() <= 0)
			Application.get().gameOver(Outcome.lost, "shields");
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
			if (!k.isCloaked() && StarMap.distance(this, k) < PHASER_RANGE) {
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
		if (!consume("energy", computeEnergyConsumption()))
			Application.get().events.fireEvent(new GameOverEvent(Outcome.lost, "Out of energy"));
		playComputerTurn();
	}

	@Override
	public void onTurnEnded(TurnEndedEvent evt) {
	}

	public void toggleAutoAim() {
		getAutoAim().setValue(!getAutoAim().getBooleanValue() && getAutoAim().isEnabled());
	}

	@Override
	public void onFire(Vessel actor, Thing target, String weapon, double damage) {
		if (target != this)
			return;
		Application.get().message(actor.getName() + " at " + actor.getLocation() + " fired on us", "damage");
		applyDamage(damage);
	}

	@Override
	public void afterFire(Vessel actor, Thing target, String weapon, double damage) {
	}
}
