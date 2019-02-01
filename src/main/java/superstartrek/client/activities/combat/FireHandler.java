package superstartrek.client.activities.combat;

import com.google.gwt.event.shared.EventHandler;

import superstartrek.client.model.Thing;

public interface FireHandler extends EventHandler{

	void onFire(Thing actor, Thing target, String weapon, int damage);
}
