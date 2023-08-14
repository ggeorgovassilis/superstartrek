package superstartrek.client.activities;

import superstartrek.client.bus.EventHandler;

public interface ActivityChangedHandler extends EventHandler{

	void onActivityChanged(String activity);
}
