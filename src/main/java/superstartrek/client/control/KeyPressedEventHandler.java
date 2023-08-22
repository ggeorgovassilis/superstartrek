package superstartrek.client.control;

import superstartrek.client.eventbus.EventHandler;

public interface KeyPressedEventHandler extends EventHandler{

	void onKeyPressed(int keyCode);
}
