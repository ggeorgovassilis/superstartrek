package superstartrek.client.activities.klingons;

import java.util.List;

import com.google.gwt.core.client.GWT;

import superstartrek.client.Application;
import superstartrek.client.activities.combat.FireEvent;
import superstartrek.client.activities.combat.FireHandler;
import superstartrek.client.activities.navigation.ThingMovedEvent;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Setting;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;

public class Klingon extends Vessel implements FireHandler, KlingonTurnHandler{

	protected final Setting disruptor;
	
	public enum ShipClass{
		
		Raider("a Klingon raider", 50, 20, "c-}"), BirdOfPrey("a Bird-of-prey",100,40,"C-D");
		
		ShipClass(String name, int shields, int disruptor, String symbol){
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
		super(app, new Setting(1,1), new Setting(c.shields, c.shields));
		setName(c.name());
		setSymbol(c.symbol);
		setCss("klingon");
		this.disruptor = new Setting(c.disruptor, c.disruptor);
		app.events.addHandler(FireEvent.TYPE, this);
		app.events.addHandler(KlingonTurnEvent.TYPE, this);
	}

	@Override
	public void onFire(Vessel actor, Thing target, String weapon, double damage) {
		if (target == this) {
			shields.decrease(damage);
			application.message(weapon+" hit "+target.getName()+" at "+target);
			if (shields.getValue()<=0) {
				application.message(target.getName()+" was destroyed "+target);
				if (target instanceof Klingon)
					target.getQuadrant().getKlingons().remove(target);
			}
		}
	}
	
	public void repositionKlingon() {
		StarMap map = application.starMap;
		Enterprise enterprise = map.enterprise;
		if (enterprise.getQuadrant() != getQuadrant())
			return;
		int dx = (int)Math.signum(enterprise.getX()-getX());
		int dy = (int)Math.signum(enterprise.getY()-getY());
		int tx = getX()+dx;
		int ty = getY()+dy;
		Thing thing = map.findThingAt(getQuadrant(), tx, ty);
		if (thing!=null)
			return;
		ThingMovedEvent event = new ThingMovedEvent(this, getQuadrant(), new Location(getX(),getY()), getQuadrant(), new Location(tx,ty));
		setX(tx);
		setY(ty);
		application.events.fireEvent(event);
	}
	
	public void fireOnEnterprise() {
		StarMap map = application.starMap;
		Enterprise enterprise = map.enterprise;
		if (enterprise.getQuadrant() != getQuadrant())
			return;
		double distance = StarMap.distance(this, enterprise);
		if (distance >2)
			return;
		List<Thing> obstacles = map.findObstaclesInLine(getQuadrant(), getX(), getY(), enterprise.getX(), enterprise.getY());
		obstacles.remove(this);
		obstacles.remove(enterprise);
		if (!obstacles.isEmpty())
			return;
		FireEvent event = new FireEvent(this, enterprise, "disruptor", disruptor.getValue());
		application.events.fireEvent(event);
	}

	@Override
	public void executeKlingonMove() {
		repositionKlingon();
		fireOnEnterprise();
	}
}
