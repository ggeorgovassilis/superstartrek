package superstartrek.client.activities.combat;

import com.google.gwt.event.shared.EventHandler;

import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;

public interface FireHandler extends EventHandler{

	void onFire(Vessel actor, Vessel target, String weapon, double damage);
}
