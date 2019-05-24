package superstartrek.client.activities.combat;

import superstartrek.client.bus.EventHandler;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;

public interface CombatHandler extends EventHandler {

	default void onFire(Quadrant quadrant, Vessel actor, Thing target, String weapon, double damage,
			boolean wasAutoFire) {
	}

	default void afterFire(Quadrant quadrant, Vessel actor, Thing target, String weapon, double damage,
			boolean wasAutoFire) {
	}

	default void onEnterpriseDamaged(Enterprise enterprise) {
	}

	default void onVesselDestroyed(Vessel klingon) {
	}

}
