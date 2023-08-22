package superstartrek.client.activities;

import superstartrek.client.eventbus.EventHandler;

public interface ActivityChangedHandler extends EventHandler{

	void onActivityChanged(String activity);
}
