package superstartrek.client.model;

import superstartrek.client.Application;
import superstartrek.client.activities.combat.FireEvent;
import superstartrek.client.activities.combat.FireHandler;

public class Star extends Thing implements FireHandler{

	public Star() {
		setName("a star");
		setSymbol("*");
		setCss("star");
		Application.get().events.addHandler(FireEvent.TYPE, this);
	}
	
	public Star(int x, int y) {
		this();
		setLocation(Location.location(x,y));
	}

	@Override
	public void onFire(Vessel actor, Thing target, String weapon, double damage) {
		if (target == this)
			Application.get().message(weapon+" hit "+getName()+" at "+getLocation());
	}

	@Override
	public void afterFire(Vessel actor, Thing target, String weapon, double damage) {
	}
}
