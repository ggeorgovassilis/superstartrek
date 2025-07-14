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
	public int randomInt(int upperBound) {
		return Random.nextInt(upperBound);
	}

	@Override
	public double randomDouble() {
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

	private static native Void _log(Object message)/*-{
	    console.log(message);
	}-*/;
	
	@Override
	public Void log(Object message) {
		return _log(message);
	}

	@Override
	public native String getBrowserReport()/*-{
var info = [];

    // --- General Browser Info ---
    info.push("--- General Browser Information ---");
    info.push("User Agent: " + navigator.userAgent);
    info.push("Platform: " + navigator.platform);
    info.push("Browser Language: " + (navigator.language || 'N/A'));
    info.push("Online Status: " + (navigator.onLine ? 'Online' : 'Offline'));
    info.push("Cookies Enabled: " + (navigator.cookieEnabled ? 'Yes' : 'No'));
    info.push("Do Not Track: " + (navigator.doNotTrack === '1' ? 'Enabled' : navigator.doNotTrack === '0' ? 'Disabled' : 'Not Specified'));
    info.push("Hardware Concurrency (CPU Cores): " + (navigator.hardwareConcurrency || 'N/A'));
    info.push("Max Touch Points: " + (navigator.maxTouchPoints || 'N/A'));
    info.push("");

    // --- Screen & Viewport Dimensions ---
    info.push("--- Screen & Viewport Dimensions ---");
    info.push("Screen Resolution: " + screen.width + "x" + screen.height + " pixels");
    info.push("Available Screen Area: " + screen.availWidth + "x" + screen.availHeight + " pixels");
    info.push("Color Depth: " + screen.colorDepth + " bits");
    info.push("Pixel Depth: " + screen.pixelDepth + " bits");
    info.push("Window Inner Size: " + window.innerWidth + "x" + window.innerHeight + " pixels (Viewport)");
    info.push("Window Outer Size: " + window.outerWidth + "x" + window.outerHeight + " pixels (Browser Window)");
    info.push("Document Body Client Size: " + document.body.clientWidth + "x" + document.body.clientHeight + " pixels");
    info.push("Device Pixel Ratio: " + window.devicePixelRatio);
    info.push("");

    // --- Browser Capabilities (Feature Detection) ---
    info.push("--- Browser Capabilities (Feature Detection) ---");
    info.push("WebAssembly Support: " + (typeof WebAssembly !== 'undefined' ? 'Yes' : 'No'));
    info.push("Service Worker Support: " + ('serviceWorker' in navigator ? 'Yes' : 'No'));
    info.push("LocalStorage Support: " + (typeof localStorage !== 'undefined' ? 'Yes' : 'No'));
    info.push("SessionStorage Support: " + (typeof sessionStorage !== 'undefined' ? 'Yes' : 'No'));
    info.push("IndexedDB Support: " + ('indexedDB' in window ? 'Yes' : 'No'));
    info.push("Geolocation Support: " + ('geolocation' in navigator ? 'Yes' : 'No'));
    info.push("WebRTC Support: " + ('RTCPeerConnection' in window ? 'Yes' : 'No'));
    info.push("Canvas Support: " + (function() {
        try {
            var canvas = document.createElement('canvas');
            return !!(canvas.getContext && canvas.getContext('2d'));
        } catch (e) {
            return 'No';
        }
    })() ? 'Yes' : 'No');
    info.push("WebGL Support: " + (function() {
        try {
            var canvas = document.createElement('canvas');
            return !!(canvas.getContext && (canvas.getContext('webgl') || canvas.getContext('experimental-webgl')));
        } catch (e) {
            return 'No';
        }
    })() ? 'Yes' : 'No');
    info.push("Touch Events Support: " + ('ontouchstart' in window ? 'Yes' : 'No'));
    info.push("Battery API Support: " + ('getBattery' in navigator ? 'Yes' : 'No'));
    info.push("Clipboard API Support: " + ('clipboard' in navigator ? 'Yes' : 'No'));
    info.push("Notifications API Support: " + ('Notification' in window ? 'Yes' : 'No'));
    info.push("Fetch API Support: " + ('fetch' in window ? 'Yes' : 'No'));
    info.push("Intersection Observer Support: " + ('IntersectionObserver' in window ? 'Yes' : 'No'));
    info.push("Resize Observer Support: " + ('ResizeObserver' in window ? 'Yes' : 'No'));
    info.push("");

    // --- URL and Document Info ---
    info.push("--- URL and Document Information ---");
    info.push("Current URL: " + window.location.href);
    info.push("Document Title: " + document.title);
    info.push("Referrer: " + (document.referrer || 'N/A'));
    info.push("Origin: " + window.location.origin);
    info.push("");

    // --- Time and Date ---
    info.push("--- Time and Date ---");
    var now = new Date();
    info.push("Current Local Time: " + now.toLocaleString());
    info.push("Timezone Offset: " + now.getTimezoneOffset() + " minutes from UTC");
    info.push("Timezone: " + (Intl.DateTimeFormat ? Intl.DateTimeFormat().resolvedOptions().timeZone : 'N/A (Intl not supported)')); // Added check for Intl support
    info.push("");

    // --- Plugins (Legacy, often empty in modern browsers) ---
    info.push("--- Plugins (Legacy) ---");
    if (navigator.plugins && navigator.plugins.length > 0) {
        for (var i = 0; i < navigator.plugins.length; i++) {
            info.push("- " + navigator.plugins[i].name + " (" + navigator.plugins[i].description + ")");
        }
    } else {
        info.push("No plugins detected or API deprecated.");
    }
    info.push("");

    return info.join('\n');
   	}-*/;

}
