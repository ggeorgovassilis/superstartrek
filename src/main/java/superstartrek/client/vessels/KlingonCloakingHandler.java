package superstartrek.client.vessels;

import superstartrek.client.eventbus.EventHandler;

public interface KlingonCloakingHandler extends EventHandler{

	default void klingonUncloaked(Klingon klingon) {};
	default void klingonCloaked(Klingon klingon) {};
}
