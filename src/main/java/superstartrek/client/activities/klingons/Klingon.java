package superstartrek.client.activities.klingons;

import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;

import superstartrek.client.Application;
import superstartrek.client.activities.combat.FireEvent;
import superstartrek.client.activities.combat.FireEvent.Phase;
import superstartrek.client.activities.combat.FireHandler;
import superstartrek.client.activities.loading.GameOverEvent.Outcome;
import superstartrek.client.activities.navigation.EnterpriseWarpedEvent;
import superstartrek.client.activities.navigation.EnterpriseWarpedHandler;
import superstartrek.client.activities.navigation.PathFinder;
import superstartrek.client.activities.navigation.PathFinderImpl;
import superstartrek.client.activities.navigation.ThingMovedEvent;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Setting;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;
import superstartrek.client.utils.Random;

public class Klingon extends Vessel implements FireHandler, KlingonTurnHandler, EnterpriseWarpedHandler {

	protected final Setting disruptor;
	private HandlerRegistration enterpriseWarpedHandler;
	private HandlerRegistration fireHandler;
	private HandlerRegistration klingonTurnHandler;
	
	protected boolean cloaked = true;
	public final static int MAX_SECTOR_SPEED = 1;
	public final static int DISRUPTOR_RANGE_SECTORS = 2;

	public enum ShipClass {

		Raider("a Klingon raider", 50, 10, "c-}"), BirdOfPrey("a Bird-of-prey", 100, 20, "C-D");

		ShipClass(String label, int shields, int disruptor, String symbol) {
			this.label = label;
			this.shields = shields;
			this.symbol = symbol;
			this.disruptor = disruptor;
		}

		public final String label;
		public final int shields;
		public final String symbol;
		public final int disruptor;
	}

	public Klingon(ShipClass c) {
		super(new Setting("impulse", 1, 1), new Setting("shields", c.shields, c.shields));
		setName(c.label);
		setSymbol(c.symbol);
		setCss("klingon cloaked");
		this.disruptor = new Setting("disruptor", c.disruptor, c.disruptor);
		enterpriseWarpedHandler = Application.get().events.addHandler(EnterpriseWarpedEvent.TYPE, this);
	}

	/*
	 * for performance reasons, only Klingons in Enterprise's quadrant need to
	 * process events, so we're registering them whenever Enterprise enters a
	 * quadrant and unregister them when it leaves.
	 */
	public void registerActionHandlers() {
		//already registered?
		if (fireHandler!=null)
			return;
		EventBus events = Application.get().events;
		fireHandler = events.addHandler(FireEvent.TYPE, this);
		klingonTurnHandler = events.addHandler(KlingonTurnEvent.TYPE, this);
	}

	public void unregisterActionHandlers() {
		//not registered?
		if (fireHandler==null)
			return;
		fireHandler.removeHandler();
		fireHandler = null;
		klingonTurnHandler.removeHandler();
		klingonTurnHandler = null;
	}

	public boolean isCloaked() {
		return cloaked;
	}

	public boolean canCloak() {
		return (impulse.isEnabled() && disruptor.isEnabled());
	}

	public void uncloak() {
		this.cloaked = false;
		setCss("klingon");
		Application.get().message(getName() + " uncloaked at " + this.getLocation(), "klingon-uncloaked");
		Application.get().events.fireEvent(new KlingonUncloakedEvent(this));
	}

	public Setting getDisruptor() {
		return disruptor;
	}

	public void repositionKlingon() {
		if (!getImpulse().isEnabled())
			return;
		StarMap map = Application.get().starMap;
		Enterprise enterprise = map.enterprise;
		if (enterprise.getQuadrant() != getQuadrant())
			return;
		// no need to move if distance is <=2 and Klingon has a clear shot at the
		// Enterprise
		if (StarMap.distance(enterprise, this) <= DISRUPTOR_RANGE_SECTORS) {
			List<Thing> obstacles = map.findObstaclesInLine(getQuadrant(), getLocation(), enterprise.getLocation());
			obstacles.remove(enterprise);
			obstacles.remove(this);
			if (obstacles.isEmpty())
				return;
		}
		PathFinder pathFinder = new PathFinderImpl();
		// path includes start and end
		List<Location> path = pathFinder.findPathBetween(this.getLocation(), enterprise.getLocation(), enterprise.getQuadrant());
		if (path == null || path.isEmpty())
			return;
		Location sector = path.get(Math.max(0, Math.min(MAX_SECTOR_SPEED, path.size() - 2)));
		jumpTo(sector);
	}

	public void jumpTo(Location dest) {
		ThingMovedEvent event = new ThingMovedEvent(this, getQuadrant(), getLocation(), getQuadrant(), dest);
		Thing obstacle = Application.get().starMap.findThingAt(getQuadrant(), dest.getX(), dest.getY());
		if (null != obstacle)
			throw new RuntimeException("There is " + obstacle.getName() + " at " + dest);
		setLocation(dest);
		Application.get().events.fireEvent(event);
	}

	public void fireOnEnterprise() {
		if (!getDisruptor().isEnabled())
			return;
		StarMap map = Application.get().starMap;
		Enterprise enterprise = map.enterprise;
		double distance = StarMap.distance(this, enterprise);
		if (distance > 2)
			return;
		List<Thing> obstacles = map.findObstaclesInLine(getQuadrant(), getLocation(), enterprise.getLocation());
		obstacles.remove(this);
		obstacles.remove(enterprise);
		if (!obstacles.isEmpty())
			return;
		if (isCloaked())
			uncloak();
		FireEvent event = new FireEvent(Phase.fire, this, enterprise, "disruptor", disruptor.getValue());
		Application.get().events.fireEvent(event);
		event = new FireEvent(Phase.afterFire, this, enterprise, "disruptor", disruptor.getValue());
		Application.get().events.fireEvent(event);
	}

	@Override
	public void executeKlingonMove() {
		StarMap map = Application.get().starMap;
		Enterprise enterprise = map.enterprise;
		if (enterprise.getQuadrant() != getQuadrant())
			return;
		repositionKlingon();
		fireOnEnterprise();
	}

	@Override
	public void destroy() {
		super.destroy();
		unregisterActionHandlers();
		enterpriseWarpedHandler.removeHandler();
		getQuadrant().getKlingons().remove(this);
		if (!Application.get().starMap.hasKlingons())
			Application.get().gameOver(Outcome.won, "");
		Application.get().message(getName()+" was destroyed", "klingon-destroyed");
		Application.get().events.fireEvent(new KlingonDestroyedEvent(this));
	}

	public void repair() {
		getImpulse().setEnabled(true);
		getDisruptor().setEnabled(true);
	}

	@Override
	public void onEnterpriseWarped(Enterprise enterprise, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
		if (qTo == this.getQuadrant()) {
			registerActionHandlers();
			this.cloaked = canCloak();
			css = "klingon " + (cloaked ? "cloaked" : "");
			Location newLocation = Application.get().starMap.findFreeSpot(getQuadrant());
			jumpTo(newLocation);
			repair();
		} else
			unregisterActionHandlers();
	}

	@Override
	public void onFire(Vessel actor, Thing target, String weapon, double damage) {
		if (target != this)
			return;
		if (isCloaked()) {
			uncloak();
			destroy();
			return;
		}
		double impact = damage / (shields.getValue() + 1);
		shields.decrease(damage);
		shields.setCurrentUpperBound(shields.getCurrentUpperBound() - damage);
		if (getImpulse().isEnabled() && Random.nextDouble() < impact)
			getImpulse().setEnabled(false);
		if (getDisruptor().isEnabled() && Random.nextDouble() < impact)
			getDisruptor().setEnabled(false);

		Application.get().message(weapon + " hit " + target.getName() + " at " + target.getLocation(), "klingon-damaged");
		if (shields.getValue() <= 0) {
			destroy();
		}
	}

	@Override
	public void afterFire(Vessel actor, Thing target, String weapon, double damage) {
	}

}
