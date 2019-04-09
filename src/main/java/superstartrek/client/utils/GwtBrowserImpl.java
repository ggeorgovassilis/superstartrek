package superstartrek.client.utils;

import com.google.gwt.user.client.Window;

public class GwtBrowserImpl implements Browser{

	@Override
	public int getWindowWidthPx() {
		return Window.getClientWidth();
	}

	@Override
	public int getWindowHeightPx() {
		return Window.getClientHeight();
	}

}
