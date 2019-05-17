package superstartrek.client.activities.klingons;

import superstartrek.client.bus.BaseHandler;

public interface KlingonDestroyedHandler extends BaseHandler{

	void onKlingonDestroyed(Klingon klingon);
}
