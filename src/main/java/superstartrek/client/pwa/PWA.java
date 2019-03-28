package superstartrek.client.pwa;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;

import superstartrek.client.Application;

public class PWA {

	AppInstallationEvent deferredInstallationPrompt;
	InstallationPrompt prompt;
	Application application;

	public static native boolean supportsServiceWorker() /*-{
															return navigator.serviceWorker!=null;
															}-*/;

	public static native void registerServiceWorker(String url) /*-{
																navigator.serviceWorker.register(url)
																.then(function(arg){
																console.log("REGISTERED SW");
																return null;
																})
																['catch'](console.error);
																}-*/;

	/*
	 * Tricky thing to remember: if the user dismisses the native installation prompt, the "beforeinstallprompt"
	 * event fires again! 
	 */
	public native void addInstallationListener() /*-{
													var that = this;
													$wnd.addEventListener('beforeinstallprompt', function (e){
													console.log("beforeinstallprompt");
													that.@superstartrek.client.pwa.PWA::installationEventCallback(Lsuperstartrek/client/pwa/AppInstallationEvent;)(e);
													});
													}-*/;

	public void installationEventCallback(AppInstallationEvent e) {
		deferredInstallationPrompt = e;
		deferredInstallationPrompt.preventDefault();
		prompt = new InstallationPrompt(this);
		application.page.add(prompt);
	}
	
	public void installApplication() {
		deferredInstallationPrompt.prompt();
		application.page.remove(prompt);
		deferredInstallationPrompt = null;
	}
	
	public PWA(Application application) {
		this.application = application;
	}

	public void dismissInstallationPrompt() {
		application.page.remove(prompt);
		deferredInstallationPrompt = null;
		prompt = null;
	}

	public void run() {
		if (!GWT.isClient())
			return;
		if (!supportsServiceWorker())
			return;
		registerServiceWorker("service-worker.js");
		addInstallationListener();
	}
}
