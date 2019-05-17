package superstartrek.client.activities.combat;

import superstartrek.client.bus.BaseHandler;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;

public interface FireHandler extends BaseHandler{

	default void onFire(Quadrant quadrant, Vessel actor, Thing target, String weapon, double damage, boolean wasAutoFire) {
	};

	default void afterFire(Quadrant quadrant, Vessel actor, Thing target, String weapon, double damage, boolean wasAutoFire) {
	};
}
