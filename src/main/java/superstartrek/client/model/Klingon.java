package superstartrek.client.model;

import superstartrek.client.Application;
import superstartrek.client.activities.combat.FireEvent;
import superstartrek.client.activities.combat.FireHandler;

public class Klingon extends Vessel implements FireHandler{
	
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
}
