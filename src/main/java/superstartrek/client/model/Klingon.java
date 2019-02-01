package superstartrek.client.model;

import superstartrek.client.Application;
import superstartrek.client.activities.combat.FireEvent;
import superstartrek.client.activities.combat.FireHandler;

public class Klingon extends Thing implements FireHandler{
	
	protected int shields = 100;
	protected final Application application;
	
	public int getShields() {
		return shields;
	}

	public Klingon(Application app) {
		this.application = app;
		setName("a Klingon raider");
		setSymbol("C-]");
		setCss("klingon");
		app.events.addHandler(FireEvent.TYPE, this);
	}

	@Override
	public void onFire(Thing actor, Thing target, String weapon, int damage) {
		if (target == this) {
			shields-=damage;
			application.message(weapon+" hit "+target.getName()+" at "+target);
		}
	}
}
