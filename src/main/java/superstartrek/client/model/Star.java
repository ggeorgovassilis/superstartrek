package superstartrek.client.model;

import superstartrek.client.Application;
import superstartrek.client.activities.combat.FireEvent;
import superstartrek.client.activities.combat.FireHandler;

public class Star extends Thing implements FireHandler{

	protected final Application application;
	
	public Star(Application application) {
		this.application = application;
		setName("a star");
		setSymbol("*");
		setCss("star");
		application.events.addHandler(FireEvent.TYPE, this);
	}

	@Override
	public void onFire(Vessel actor, Thing target, String weapon, double damage) {
		if (target == this)
			application.message(weapon+" hit "+getName()+" at "+this);
	}

	@Override
	public void afterFire(Vessel actor, Thing target, String weapon, double damage) {
	}
}
