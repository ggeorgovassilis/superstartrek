package superstartrek.client.activities.klingons;

import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;

import superstartrek.client.Application;
import superstartrek.client.activities.combat.FireHandler;
import superstartrek.client.activities.navigation.EnterpriseWarpedHandler;
import superstartrek.client.activities.navigation.PathFinder;
import superstartrek.client.activities.navigation.PathFinderImpl;
import superstartrek.client.activities.navigation.ThingMovedHandler.ThingMovedEvent;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.GameRestartEvent;
import superstartrek.client.control.KlingonTurnStartedEvent;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.QuadrantIndex;
import superstartrek.client.model.Setting;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;
import superstartrek.client.utils.BrowserAPI;

public class Klingon extends Vessel implements FireHandler, GamePhaseHandler, EnterpriseWarpedHandler {

	protected final Setting disruptor;
	protected final Setting cloak;
	
	private HandlerRegistration enterpriseWarpedHandler;
	private HandlerRegistration fireHandler;
	private HandlerRegistration klingonTurnHandler;
	private HandlerRegistration gameRestartHandler;
	
	public final static int MAX_SECTOR_SPEED = 1;
	public final static int DISRUPTOR_RANGE_SECTORS = 2;
	
	public final ShipClass shipClass;

	public enum ShipClass {

		Raider("a Klingon raider", 50, 10, "<div class=vessel><span class=bridge>c</span><span class=fuselage>-</span><span class=wings>}</span></div>"), BirdOfPrey("a Bird-of-prey", 100, 20, "<div class=vessel><span class=bridge>C</span><span class=fuselage>-</span><span class=wings>D</span></div>");

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
		this.shipClass = c;
		cloak = new Setting("cloak", 1, 1);
		setName(c.label);
		setSymbol(c.symbol);
		setCss("klingon cloaked");
		this.disruptor = new Setting("disruptor", c.disruptor, c.disruptor);
		enterpriseWarpedHandler = Application.get().events.addHandler(EnterpriseWarpedEvent.TYPE, this);
		gameRestartHandler = Application.get().events.addHandler(GameRestartEvent.TYPE, this);
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
		klingonTurnHandler = events.addHandler(KlingonTurnStartedEvent.TYPE, this);
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
		Application.get().events.fireEvent(new KlingonCloakingHandler.KlingonUncloakedEvent(this));
	}

	public Setting getDisruptor() {
		return disruptor;
	}
	
	public boolean hasClearShotAt(QuadrantIndex index, Location target, Enterprise enterprise, StarMap map) {
		if (StarMap.within_distance(target, getLocation(), DISRUPTOR_RANGE_SECTORS)) {
			List<Thing> obstacles = map.findObstaclesInLine(index, getLocation(), target,2);
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
		// no need to move if distance is <=2 and Klingon has a clear shot at the
		// Enterprise
		QuadrantIndex index = new QuadrantIndex(enterprise.getQuadrant(), map);
		if (hasClearShotAt(index, enterprise.getLocation(), enterprise, map))
			return;
		PathFinder pathFinder = new PathFinderImpl();
		// path includes start and end
		List<Location> path = pathFinder.findPathBetween(this.getLocation(), enterprise.getLocation(), enterprise.getQuadrant(), map);
		if (path.isEmpty())
			return;
		//path used to contain origin sector (old a* impl); it doesn't anymore, that's why MAX_SECTOR_SPEED-1
		Location sector = path.get(Math.max(0, Math.min(MAX_SECTOR_SPEED-1, path.size() - 2)));
		jumpTo(sector);
	}

	public void jumpTo(Location dest) {
		Application app = Application.get();
		Quadrant quadrant = app.getActiveQuadrant();
		ThingMovedEvent event = new ThingMovedEvent(this, quadrant, getLocation(), quadrant, dest);
		Thing obstacle = quadrant.findThingAt(dest);
		if (null != obstacle)
			throw new RuntimeException("There is " + obstacle.getName() + " at " + dest);
		setLocation(dest);
		app.events.fireEvent(event);
	}

	public void fireOnEnterprise() {
		if (!getDisruptor().isEnabled())
			return;
		Application app = Application.get();
		StarMap map = app.starMap;
		Enterprise enterprise = map.enterprise;
		boolean inRange = StarMap.within_distance(this, enterprise, DISRUPTOR_RANGE_SECTORS);
		if (!inRange)
			return;
		QuadrantIndex index = new QuadrantIndex(enterprise.getQuadrant(), map);
		List<Thing> obstacles = map.findObstaclesInLine(index, getLocation(), enterprise.getLocation(),2);
		obstacles.remove(this);
		obstacles.remove(enterprise);
		if (!obstacles.isEmpty())
			return;
		if (!isVisible())
			uncloak();
		FireEvent event = new FireEvent(FireEvent.Phase.fire, enterprise.getQuadrant(), this, enterprise, "disruptor", disruptor.getValue(), true);
		app.events.fireEvent(event);
		event = new FireEvent(FireEvent.Phase.afterFire, enterprise.getQuadrant(), this, enterprise, "disruptor", disruptor.getValue(), true);
		app.events.fireEvent(event);
	}
	
	
	public void cloak() {
		getCloak().setValue(true);
		Application.get().events.fireEvent(new KlingonCloakingHandler.KlingonCloakedEvent(this));
		Application.get().message(getName() + " cloaked at " + this.getLocation(), "klingon-uncloaked");
	}
	
	public void flee() {
		if (canCloak() && isVisible()) {
			cloak();
		}
		if (!getImpulse().isEnabled()) {
			return;
		}
		Application app = Application.get();
		double distance = StarMap.distance(getLocation(), app.starMap.enterprise.getLocation());
		if (getImpulse().isEnabled() && getImpulse().getValue()>=1) {
			int triesLeft = 5;
			Location loc = null;
			do {
				loc = app.starMap.findFreeSpotAround(app.getActiveQuadrant(), getLocation(), 1+(int)getImpulse().getValue());
				if (loc!=null) {
					double newDistance = StarMap.distance(app.starMap.enterprise.getLocation(), loc);
					if (newDistance<=distance)
						loc = null;
				}
				triesLeft--;
			} while(triesLeft>0 && loc==null);
			if (loc!=null)
				jumpTo(loc);
		}
	}

	@Override
	public void onKlingonTurnStarted() {
		//Reminder: only klingons in the active sector receive this event
		Application app = Application.get();
		if (app.getFlags().contains("nopc"))
			return;
		if (!getDisruptor().isEnabled())
			flee();
		else
			repositionKlingon();
		fireOnEnterprise();
	}

	@Override
	public void destroy() {
		unregisterActionHandlers();
		enterpriseWarpedHandler.removeHandler();
		gameRestartHandler.removeHandler();
		Application app = Application.get();
		app.getActiveQuadrant().getKlingons().remove(this);
		app.message(getName()+" was destroyed", "klingon-destroyed");
		super.destroy();
		app.events.fireEvent(new KlingonDestroyedHandler.KlingonDestroyedEvent(this));
	}

	public void repair() {
		getImpulse().setEnabled(true);
		getDisruptor().setEnabled(true);
		cloak.setEnabled(true);
		getShields().setCurrentUpperBound(Math.max(getShields().getMaximum()/2, getShields().getCurrentUpperBound()));
		getShields().setValue(getShields().getCurrentUpperBound());
	}

	@Override
	public void onEnterpriseWarped(Enterprise enterprise, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
		//TODO: contains() is a slow check and all klingons in all quadrants react to this event
		if (qTo.contains(this)) {
			registerActionHandlers();
			repair();
			cloak.setValue(canCloak());
			css = "klingon " + (isVisible() ? "" : "cloaked");
			//TODO: this method is called for every klingon in the quadrant. findFreeSpot builds an index each time, which
			//might be slow.
			Location newLocation = Application.get().starMap.findFreeSpot(qTo);
			jumpTo(newLocation);
		} else
			unregisterActionHandlers();
	}

	@Override
	public void onFire(FireEvent evt) {
		if (evt.target != this)
			return;
		if (!isVisible()) {
			uncloak();
			destroy();
			return;
		}
		double impact = evt.damage / (shields.getValue() + 1);
		shields.decrease(evt.damage);
		BrowserAPI random = Application.get().browserAPI;
		shields.setCurrentUpperBound(shields.getCurrentUpperBound() - evt.damage);
		if (getImpulse().isEnabled() && random.nextDouble() < impact)
			getImpulse().setEnabled(false);
		if (getDisruptor().isEnabled() && random.nextDouble() < impact)
			getDisruptor().setEnabled(false);
		if (getCloak().isEnabled() && random.nextDouble() < impact)
			getCloak().setEnabled(false);

		Application.get().message(evt.weapon + " hit " + evt.target.getName() + " at " + evt.target.getLocation(), "klingon-damaged");
		if (shields.getValue() <= 0) {
			destroy();
		}
	}
	
	@Override
	public boolean isVisible() {
		return !cloak.getBooleanValue();
	}
	
	public static boolean isCloakedKlingon(Thing thing){
		return (Klingon.is(thing)) && !thing.isVisible();
	}
	
	public static boolean isEmptyOrCloakedKlingon(Thing thing) {
		return thing == null || isCloakedKlingon(thing);
	}
	
	public static boolean is(Thing thing) {
		return thing instanceof Klingon;
	}

	public static Klingon as(Thing thing) {
		return (Klingon)thing;
	}
	
	@Override
	public void beforeGameRestart() {
		unregisterActionHandlers();
	}
}
