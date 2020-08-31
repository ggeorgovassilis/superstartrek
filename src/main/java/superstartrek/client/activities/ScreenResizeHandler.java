package superstartrek.client.activities;

import superstartrek.client.bus.EventHandler;

public interface ScreenResizeHandler extends EventHandler{
	
	void onScreenResize();
	void onAfterScreenResize(int widthPx, int heightPx);

}
