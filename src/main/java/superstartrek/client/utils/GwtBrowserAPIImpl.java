package superstartrek.client.utils;

import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;

public class GwtBrowserAPIImpl implements BrowserAPI{

	@Override
	public int getWindowWidthPx() {
		return Window.getClientWidth();
	}

	@Override
	public int getWindowHeightPx() {
		return Window.getClientHeight();
	}

	@Override
	public int nextInt(int upperBound) {
		return Random.nextInt(upperBound);
	}

	@Override
	public double nextDouble() {
		return Random.nextDouble();
	}

}
