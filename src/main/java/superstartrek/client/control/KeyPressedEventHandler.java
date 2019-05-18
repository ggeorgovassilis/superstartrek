package superstartrek.client.control;

import superstartrek.client.bus.BaseHandler;

public interface KeyPressedEventHandler extends BaseHandler{

	void onKeyPressed(int keyCode);
}
