package superstartrek.client.utils;

import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;

import superstartrek.client.activities.pwa.Callback;

public interface BrowserAPI {

	int getWindowWidthPx();
	int getWindowHeightPx();
	
	int getMetricWidthInPx();
	int getMetricHeightInPx();

	
	boolean hasKeyboard();
	
	int nextInt(int upperBound);
	double nextDouble();
	
	HandlerRegistration addHistoryListener(ValueChangeHandler<String> handler);
	Void postHistoryChange(String token);
	Void postHistoryChange(String token, boolean issueEvent);
	Void confirm(String message, Callback<Boolean> answer);
	Void reloadApplication();
	String getParameter(String param);
	Set<String> getFlags();

}
