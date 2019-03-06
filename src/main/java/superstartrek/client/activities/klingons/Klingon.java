package superstartrek.client.activities.klingons;

import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;

import superstartrek.client.Application;
import superstartrek.client.activities.combat.FireEvent;
import superstartrek.client.activities.combat.FireEvent.Phase;
import superstartrek.client.activities.combat.FireHandler;
import superstartrek.client.activities.loading.GameOverEvent.Outcome;
import superstartrek.client.activities.navigation.EnterpriseWarpedEvent;
import superstartrek.client.activities.navigation.EnterpriseWarpedHandler;
import superstartrek.client.activities.navigation.PathFinder;
import superstartrek.client.activities.navigation.ThingMovedEvent;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Setting;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;

public class Klingon extends Vessel implements FireHandler, KlingonTurnHandler, EnterpriseWarpedHandler {

	protected final Setting disruptor;
	protected HandlerRegistration[] handlers = new HandlerRegistration[3];
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

	public Klingon(Application app, ShipClass c) {
		super(app, new Setting("impulse", 1, 1), new Setting("shields", c.shields, c.shields));
		setName(c.label);
		setSymbol(c.symbol);
		setCss("klingon cloaked");
		this.disruptor = new Setting("disruptor", c.disruptor, c.disruptor);
		handlers[0] = app.events.addHandler(FireEvent.TYPE, this);
		handlers[1] = app.events.addHandler(KlingonTurnEvent.TYPE, this);
		handlers[2] = app.events.addHandler(EnterpriseWarpedEvent.TYPE, this);
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
		application.message(getName()+" uncloaked at "+this.getLocation(),"klingon-uncloaked");
		application.events.fireEvent(new KlingonUncloakedEvent(this));
	}
	
	public Setting getDisruptor() {
		return disruptor;
	}

	public void repositionKlingon() {
		StarMap map = application.starMap;
		Enterprise enterprise = map.enterprise;
		if (enterprise.getQuadrant()!=getQuadrant())
			return;
		//no need to move if distance is <=2 and Klingon has a clear shot at the Enterprise
		if (StarMap.distance(enterprise, this)<=DISRUPTOR_RANGE_SECTORS) {
			List<Thing> obstacles = map.findObstaclesInLine(getQuadrant(), getLocation(), enterprise.getLocation());
			obstacles.remove(enterprise);
			obstacles.remove(this);
			if (obstacles.isEmpty())
				return;
		}
		PathFinder pathFinder = new PathFinder(getQuadrant());
		//path includes start and end
		List<Location> path = pathFinder.findPathBetween(this.getLocation(), enterprise.getLocation());
		if (path == null || path.isEmpty())
			return;
		Location sector = path.get(Math.max(0,Math.min(MAX_SECTOR_SPEED, path.size()-2)));
		jumpTo(sector);
	}
	
	public void jumpTo(Location dest) {
		ThingMovedEvent event = new ThingMovedEvent(this, getQuadrant(), getLocation(), getQuadrant(),
				dest);
		Thing obstacle = application.starMap.findThingAt(getQuadrant(), dest.getX(), dest.getY());
		if (null!=obstacle)
			throw new RuntimeException("There is "+obstacle.getName()+" at "+dest);
		setLocation(new Location(dest));
		application.events.fireEvent(event);
	}

	public void fireOnEnterprise() {
		StarMap map = application.starMap;
		Enterprise enterprise = map.enterprise;
		double distance = StarMap.distance(this, enterprise);
		if (distance > 2)
			return;
		List<Thing> obstacles = map.findObstaclesInLine(getQuadrant(), getLocation(), 
				enterprise.getLocation());
		obstacles.remove(this);
		obstacles.remove(enterprise);
		if (!obstacles.isEmpty())
			return;
		if (isCloaked())
			uncloak();
		FireEvent event = new FireEvent(Phase.fire, this, enterprise, "disruptor", disruptor.getValue());
		application.events.fireEvent(event);
		event = new FireEvent(Phase.afterFire, this, enterprise, "disruptor", disruptor.getValue());
		application.events.fireEvent(event);
	}

	@Override
	public void executeKlingonMove() {
		StarMap map = application.starMap;
		Enterprise enterprise = map.enterprise;
		if (enterprise.getQuadrant() != getQuadrant())
			return;
		repositionKlingon();
		fireOnEnterprise();
	}

	@Override
	public void destroy() {
		super.destroy();
		for (HandlerRegistration hr:handlers)
			hr.removeHandler();
		getQuadrant().getKlingons().remove(this);
		if (!application.starMap.hasKlingons())
			application.gameOver(Outcome.won, "");
		application.events.fireEvent(new KlingonDestroyedEvent(this));
	}

	@Override
	public void onEnterpriseWarped(Enterprise enterprise, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
		if (qTo == this.getQuadrant()) {
			this.cloaked = canCloak();
			css = "klingon "+(cloaked?"cloaked":"");
			Location newLocation = getApplication().starMap.findFreeSpot(getQuadrant());
			jumpTo(newLocation);
		}
	}

	@Override
	public void onFire(Vessel actor, Thing target, String weapon, double damage) {
		if (target != this)
			return;
		if (isCloaked()) {
			destroy();
			return;
		}
		shields.decrease(damage);
		shields.setCurrentUpperBound(shields.getCurrentUpperBound() - damage);
		application.message(weapon + " hit " + target.getName() + " at " + target.getLocation(), "klingon-damaged");
		if (shields.getValue() <= 0) {
			application.message(target.getName() + " was destroyed by " + actor.getName(), "klingon-damaged");
			destroy();
		}
	}

	@Override
	public void afterFire(Vessel actor, Thing target, String weapon, double damage) {
	}
	
}
