package superstartrek.client.vessels;

import superstartrek.client.eventbus.EventHandler;
import superstartrek.client.space.Quadrant;
import superstartrek.client.space.Thing;

public interface CombatHandler extends EventHandler {

	enum partTarget{none, weapons, propulsion}
	
	default void onFire(Quadrant quadrant, Vessel actor, Thing target, Weapon weapon, double damage,
			boolean wasAutoFire, partTarget partTarget) {
	}

	default void afterFire(Quadrant quadrant, Vessel actor, Thing target, Weapon weapon, double damage,
			boolean wasAutoFire) {
	}

	default void onEnterpriseDamaged(Enterprise enterprise) {
	}

	default void onVesselDestroyed(Vessel klingon) {
	}

}
