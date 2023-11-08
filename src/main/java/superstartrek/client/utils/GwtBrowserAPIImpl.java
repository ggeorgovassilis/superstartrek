package superstartrek.client.utils;

import java.util.Set;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

import superstartrek.client.activities.Presenter;
import superstartrek.client.activities.View;
import superstartrek.client.activities.pwa.Callback;
import superstartrek.client.eventbus.EventBus;
import superstartrek.client.eventbus.Events;

public class GwtBrowserAPIImpl implements BrowserAPI, ResizeHandler, KeyDownHandler {

	Set<String> flags;
	int emMetricOffsetWidth;
	int emMetricOffsetHeight;
	RootPanel rootPanel;
	EventBus bus;

	public GwtBrowserAPIImpl(EventBus bus) {
		this.bus = bus;
		Window.addResizeHandler(this);
		rootPanel = RootPanel.get();
		rootPanel.addDomHandler(this, KeyDownEvent.getType());
		updateMetrics();
		//TODO: for unknown reasons, metrics are 0 in chrome direct after loading. after postponing, they read fine.
		Timer.postpone(()->updateMetrics());
	}

	void updateMetrics() {
		Element e = Document.get().getElementById("em-metric");
		emMetricOffsetHeight = e.getOffsetHeight();
		emMetricOffsetWidth = e.getOffsetWidth();
	}

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
	public HandlerRegistration addHistoryHandler(ValueChangeHandler<String> handler) {
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
		// TODO: this is a bad heuristic. maybe listen for key press events; is there
		// are any, this
		// device has a keyboard?
		return getWindowWidthPx() > 400;
	}

	@Override
	public String getParameter(String param) {
		return Window.Location.getParameter(param);
	}

	@Override
	public int getMetricWidthInPx() {
		return emMetricOffsetWidth;
	}

	@Override
	public int getMetricHeightInPx() {
		return emMetricOffsetHeight;
	}

	@Override
	public void onResize(ResizeEvent event) {
		// metrics change only when document zoom changes (in that case this method is
		// invoked).
		// TODO: it's possible that metrics don't change even then as zooming is
		// transparent to the app.
		updateMetrics();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <P extends Presenter> void addToPage(View<P> view) {
		rootPanel.add(view);
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		bus.fireEvent(Events.KEY_PRESSED, (h) -> h.onKeyPressed(event.getNativeKeyCode()));
	}

	@Override
	public String getCookie(String name) {
		return Cookies.getCookie(name);
	}

	@Override
	public void setCookie(String name, String value) {
		Cookies.setCookie(name, value);
	}

	@Override
	public String getLocallyStoredValue(String key) {
		return Storage.getLocalStorageIfSupported().getItem(key);
	}

	@Override
	public Void storeValueLocally(String key, String value) {
		Storage.getLocalStorageIfSupported().setItem(key, value);
		return null;
	}

	@Override
	public void deleteValueLocally(String key) {
		Storage.getLocalStorageIfSupported().removeItem(key);
	}

	@Override
	public native Element createElementNs(String nameSpace, String tag)/*-{
			return document.createElementNS(nameSpace,tag);
	}-*/;

	@Override
	public Void addGlobalCss(String css) {
		rootPanel.addStyleName(css);
		return null;
	}

	@Override
	public Void removeGlobalCss(String css) {
		rootPanel.removeStyleName(css);
		return null;
	}

	@Override
	public HandlerRegistration addWindowResizeHandler(ResizeHandler handler) {
		return Window.addResizeHandler(handler);
	}

	@Override
	public Void alert(String message) {
		Window.alert(message);
		return null;
	}

	@Override
	public String getAppBuildNr() {
		return Document.get().getElementById("appBuildNr").getAttribute("content");
	}

	@Override
	public String[] parseStringJsonArray(String json) {
		Object jsarr = (JSONArray)JSONParser.parseStrict(json);
		return (String[])jsarr;
	}

	@Override
	public String toJson(String[] array) {
		JSONArray jsarr = (JSONArray)((Object)array);
		return jsarr.toString();
	}


}
