package superstartrek.client.activities.klingons;

import superstartrek.client.bus.BaseHandler;

public interface KlingonCloakingHandler extends BaseHandler{

	default void klingonUncloaked(Klingon klingon) {};
	default void klingonCloaked(Klingon klingon) {};
}
