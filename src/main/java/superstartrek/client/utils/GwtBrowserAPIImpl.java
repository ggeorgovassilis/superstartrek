package superstartrek.client.utils;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;

import superstartrek.client.activities.pwa.Callback;

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

	@Override
	public HandlerRegistration addHistoryListener(ValueChangeHandler<String> handler) {
		return History.addValueChangeHandler(handler);
	}

	@Override
	public Void postHistoryChange(String token) {
		History.newItem(token);
		return null;
	}

	@Override
	public Void confirm(String message, Callback<Boolean> answer) {
		boolean result = Window.confirm(message);
		answer.onSuccess(result);
		return null;
	}

	@Override
	public Void postHistoryChange(String token, boolean issueEvent) {
		History.newItem(token, issueEvent);
		return null;
	}

	@Override
	public Void reloadApplication() {
		Window.Location.reload();
		return null;
	}

	@Override
	public boolean hasKeyboard() {
		return getWindowWidthPx()>1000;
	}

}
