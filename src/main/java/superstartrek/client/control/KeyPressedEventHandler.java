package superstartrek.client.control;

import superstartrek.client.bus.EventHandler;

public interface KeyPressedEventHandler extends EventHandler{

	void onKeyPressed(int keyCode);
}
