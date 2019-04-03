package superstartrek.client.activities.klingons;

import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;

import superstartrek.client.Application;
import superstartrek.client.activities.combat.FireHandler;
import superstartrek.client.activities.navigation.EnterpriseWarpedEvent;
import superstartrek.client.activities.navigation.EnterpriseWarpedHandler;
import superstartrek.client.activities.navigation.PathFinder;
import superstartrek.client.activities.navigation.PathFinderImpl;
import superstartrek.client.activities.navigation.ThingMovedEvent;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.KlingonTurnEvent;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Setting;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;
import superstartrek.client.utils.Random;

public class Klingon extends Vessel implements FireHandler, GamePhaseHandler, EnterpriseWarpedHandler {

	protected final Setting disruptor;
	protected final Setting cloak;
	
	private HandlerRegistration enterpriseWarpedHandler;
	private HandlerRegistration fireHandler;
	private HandlerRegistration klingonTurnHandler;
	
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
		cloak = new Setting("cloak", 1, 1);
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
		return cloak.getBooleanValue();
	}

	public boolean canCloak() {
		return cloak.isEnabled();
	}
	
	public Setting getCloak() {
		return cloak;
	}

	public void uncloak() {
		cloak.setValue(0);
		setCss("klingon");
		Application.get().message(getName() + " uncloaked at " + this.getLocation(), "klingon-uncloaked");
		Application.get().events.fireEvent(new KlingonUncloakedHandler.KlingonUncloakedEvent(this));
	}

	public Setting getDisruptor() {
		return disruptor;
	}
	
	public boolean hasClearShotAt(Location target, Enterprise enterprise, StarMap map) {
		if (StarMap.distance(target, getLocation()) <= DISRUPTOR_RANGE_SECTORS) {
			List<Thing> obstacles = map.findObstaclesInLine(getQuadrant(), getLocation(), target);
			obstacles.remove(enterprise);
			obstacles.remove(this);
			if (obstacles.isEmpty())
				return true;
		}
		return false;
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
		if (hasClearShotAt(enterprise.getLocation(), enterprise, map))
			return;
		PathFinder pathFinder = new PathFinderImpl();
		// path includes start and end
		List<Location> path = pathFinder.findPathBetween(this.getLocation(), enterprise.getLocation(), enterprise.getQuadrant(), map);
		if (path == null || path.isEmpty())
			return;
		//path used to contain origin sector (old a* impl); it doesn't anymore, that's why MAX_SECTOR_SPEED-1
		Location sector = path.get(Math.max(0, Math.min(MAX_SECTOR_SPEED-1, path.size() - 2)));
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
		FireEvent event = new FireEvent(FireEvent.Phase.fire, this, enterprise, "disruptor", disruptor.getValue(), true);
		Application.get().events.fireEvent(event);
		event = new FireEvent(FireEvent.Phase.afterFire, this, enterprise, "disruptor", disruptor.getValue(), true);
		Application.get().events.fireEvent(event);
	}

	@Override
	public void executeKlingonMove() {
		Application app = Application.get();
		if (app.getFlags().contains("nopc"))
			return;
		StarMap map = app.starMap;
		Enterprise enterprise = map.enterprise;
		if (enterprise.getQuadrant() != getQuadrant())
			return;
		repositionKlingon();
		fireOnEnterprise();
	}

	@Override
	public void destroy() {
		unregisterActionHandlers();
		enterpriseWarpedHandler.removeHandler();
		getQuadrant().getKlingons().remove(this);
		Application.get().message(getName()+" was destroyed", "klingon-destroyed");
		super.destroy();
		Application.get().events.fireEvent(new KlingonDestroyedHandler.KlingonDestroyedEvent(this));
	}

	public void repair() {
		getImpulse().setEnabled(true);
		getDisruptor().setEnabled(true);
		cloak.setEnabled(true);
	}

	@Override
	public void onEnterpriseWarped(Enterprise enterprise, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
		if (qTo == this.getQuadrant()) {
			registerActionHandlers();
			cloak.setValue(canCloak());
			css = "klingon " + (isCloaked() ? "cloaked" : "");
			Location newLocation = Application.get().starMap.findFreeSpot(getQuadrant());
			jumpTo(newLocation);
			repair();
		} else
			unregisterActionHandlers();
	}

	@Override
	public void onFire(FireEvent evt) {
		if (evt.target != this)
			return;
		if (isCloaked()) {
			uncloak();
			destroy();
			return;
		}
		double impact = evt.damage / (shields.getValue() + 1);
		shields.decrease(evt.damage);
		shields.setCurrentUpperBound(shields.getCurrentUpperBound() - evt.damage);
		if (getImpulse().isEnabled() && Random.nextDouble() < impact)
			getImpulse().setEnabled(false);
		if (getDisruptor().isEnabled() && Random.nextDouble() < impact)
			getDisruptor().setEnabled(false);

		Application.get().message(evt.weapon + " hit " + evt.target.getName() + " at " + evt.target.getLocation(), "klingon-damaged");
		if (shields.getValue() <= 0) {
			destroy();
		}
	}

}
