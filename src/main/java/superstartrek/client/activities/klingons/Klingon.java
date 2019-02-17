package superstartrek.client.activities.klingons;

import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;

import superstartrek.client.Application;
import superstartrek.client.activities.combat.FireEvent;
import superstartrek.client.activities.combat.FireHandler;
import superstartrek.client.activities.loading.GameOverEvent.Outcome;
import superstartrek.client.activities.navigation.EnterpriseWarpedEvent;
import superstartrek.client.activities.navigation.EnterpriseWarpedHandler;
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

	public enum ShipClass {

		Raider("a Klingon raider", 50, 8, "c-}"), BirdOfPrey("a Bird-of-prey", 100, 20, "C-D");

		ShipClass(String name, int shields, int disruptor, String symbol) {
			this.name = name;
			this.shields = shields;
			this.symbol = symbol;
			this.disruptor = disruptor;
		}

		final String name;
		final int shields;
		final String symbol;
		final int disruptor;
	}

	public Klingon(Application app, ShipClass c) {
		super(app, new Setting("impulse", 1, 1), new Setting("shields", c.shields, c.shields));
		setName(c.name());
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
		application.message(getName()+" uncloaked at "+this);
		application.events.fireEvent(new KlingonUncloakedEvent(this));
	}
	
	public Setting getDisruptor() {
		return disruptor;
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
		application.message(weapon + " hit " + target.getName() + " at " + target);
		if (shields.getValue() <= 0) {
			application.message(target.getName() + " was destroyed " + target);
			if (target instanceof Klingon)
				destroy();
		}
	}

	public void repositionKlingon() {
		StarMap map = application.starMap;
		Enterprise enterprise = map.enterprise;
		int dx = (int) Math.signum(enterprise.getX() - getX());
		int dy = (int) Math.signum(enterprise.getY() - getY());
		int tx = getX() + dx;
		int ty = getY() + dy;
		Thing thing = map.findThingAt(getQuadrant(), tx, ty);
		if (thing != null)
			return;
		ThingMovedEvent event = new ThingMovedEvent(this, getQuadrant(), new Location(getX(), getY()), getQuadrant(),
				new Location(tx, ty));
		setX(tx);
		setY(ty);
		application.events.fireEvent(event);
	}

	public void fireOnEnterprise() {
		StarMap map = application.starMap;
		Enterprise enterprise = map.enterprise;
		double distance = StarMap.distance(this, enterprise);
		if (distance > 2)
			return;
		List<Thing> obstacles = map.findObstaclesInLine(getQuadrant(), getX(), getY(), enterprise.getX(),
				enterprise.getY());
		obstacles.remove(this);
		obstacles.remove(enterprise);
		if (!obstacles.isEmpty())
			return;
		if (isCloaked())
			uncloak();
		FireEvent event = new FireEvent(this, enterprise, "disruptor", disruptor.getValue());
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
			application.gameOver(Outcome.won);
	}

	@Override
	public void onEnterpriseWarped(Enterprise enterprise, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
		if (enterprise.getQuadrant() == this.getQuadrant()) {
			this.cloaked = !cloaked && canCloak();
			css = "klingon cloaked";
		}
	}
	
}
