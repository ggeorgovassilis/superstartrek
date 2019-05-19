package superstartrek.client.utils;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;

import superstartrek.client.activities.pwa.Callback;

public class GwtBrowserAPIImpl implements BrowserAPI {

	Set<String> flags;

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
	public Void postHistoryChange(String token, boolean issueEvent) {
		History.newItem(token, issueEvent);
		return null;
	}

	@Override
	public Void confirm(String message, Callback<Boolean> answer) {
		boolean result = Window.confirm(message);
		answer.onSuccess(result);
		return null;
	}

	@Override
	public Void reloadApplication() {
		Window.Location.reload();
		return null;
	}

	@Override
	public boolean hasKeyboard() {
		return getWindowWidthPx() > 400;
	}

	@Override
	public String getParameter(String param) {
		return Window.Location.getParameter(param);
	}

	@Override
	public Set<String> getFlags() {
		if (flags != null)
			return flags;
		String sflags = getParameter("flags");
		if (sflags == null)
			sflags = "";
		String[] split = sflags.split(",");
		flags = new HashSet<>();
		for (String s : split)
			flags.add(s);
		return flags;
	}

}
