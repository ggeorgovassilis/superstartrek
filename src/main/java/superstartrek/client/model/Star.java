package superstartrek.client.model;

import superstartrek.client.Application;
import superstartrek.client.activities.combat.FireEvent;
import superstartrek.client.activities.combat.FireHandler;

public class Star extends Thing implements FireHandler{

	public Star(int x, int y, boolean registerEvents) {
		setName("a star");
		setSymbol("*");
		setCss("star");
		if (registerEvents)
			Application.get().events.addHandler(FireEvent.TYPE, this);
		setLocation(Location.location(x,y));
	}

	public Star(int x, int y) {
		this(x,y, true);
	}

	@Override
	public void onFire(FireEvent evt) {
		if (evt.target == this)
			Application.get().message(evt.weapon+" hit "+getName()+" at "+getLocation());
	}

}
