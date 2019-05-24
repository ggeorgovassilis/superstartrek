package superstartrek.client.activities.klingons;

import superstartrek.client.bus.EventHandler;

public interface KlingonCloakingHandler extends EventHandler{

	default void klingonUncloaked(Klingon klingon) {};
	default void klingonCloaked(Klingon klingon) {};
}
