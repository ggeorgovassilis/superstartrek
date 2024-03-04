package superstartrek.client.uihandler;

import superstartrek.client.eventbus.EventHandler;

public interface InteractionHandler extends EventHandler{

	void onUiInteraction(String tag);
}
