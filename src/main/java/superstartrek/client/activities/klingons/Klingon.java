package superstartrek.client.activities.klingons;

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
	
	public enum ShipClass{
		
		Raider("a Klingon raider", 50, "c-}"), BirdOfPrey("a Bird-of-prey",100,"C-D");
		
		ShipClass(String name, int shields, String symbol){
			this.name = name;
			this.shields = shields;
			this.symbol = symbol;
		}
		final String name;
		final int shields;
		final String symbol;
	}

	public Klingon(Application app, ShipClass c) {
		super(app, new Setting(1,1), new Setting(c.shields, c.shields));
		setName(c.name());
		setSymbol(c.symbol);
		setCss("klingon");
		app.events.addHandler(FireEvent.TYPE, this);
		app.events.addHandler(KlingonTurnEvent.TYPE, this);
	}

	@Override
	public void onFire(Vessel actor, Vessel target, String weapon, double damage) {
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

	@Override
	public void move() {
		StarMap map = application.starMap;
		Enterprise enterprise = map.enterprise;
		if (enterprise.getQuadrant() != getQuadrant())
			return;
		int dx = (int)Math.signum(enterprise.getX()-getX());
		int dy = (int)Math.signum(enterprise.getY()-getY());
		int tx = getX()+dx;
		int ty = getY()+dy;
		GWT.log(this+" "+dx+":"+dy+" "+tx+":"+ty);
		Thing thing = map.findThingAt(getQuadrant(), tx, ty);
		if (thing!=null)
			return;
		ThingMovedEvent event = new ThingMovedEvent(this, getQuadrant(), new Location(getX(),getY()), getQuadrant(), new Location(tx,ty));
		setX(tx);
		setY(ty);
		application.events.fireEvent(event);
	}
}
