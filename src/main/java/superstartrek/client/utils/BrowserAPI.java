package superstartrek.client.utils;

import java.util.Set;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;

import superstartrek.client.activities.Presenter;
import superstartrek.client.activities.View;
import superstartrek.client.activities.pwa.Callback;

public interface BrowserAPI {

	int getWindowWidthPx();
	int getWindowHeightPx();
	
	int getMetricWidthInPx();
	int getMetricHeightInPx();

	
	boolean hasKeyboard();
	
	String getCookie(String name);
	void setCookie(String name, String value);
	int nextInt(int upperBound);
	double nextDouble();
	
	HandlerRegistration addHistoryListener(ValueChangeHandler<String> handler);
	Void postHistoryChange(String token);
	Void postHistoryChange(String token, boolean issueEvent);
	Void confirm(String message, Callback<Boolean> answer);
	Void alert(String message);
	Void reloadApplication();
	String getParameter(String param);
	Set<String> getFlags();
	
	@SuppressWarnings("rawtypes")
	<P extends Presenter> void addToPage(View<P> view);
	
	String getLocallyStoredValue(String key);
	void storeValueLocally(String key, String value);
	void deleteValueLocally(String key);
	
	Element createElementNs(String nameSpace, String tag);
	Void addGlobalCss(String css);
	Void removeGlobalCss(String css);
	HandlerRegistration addWindowResizeHandler(ResizeHandler handler);

}
