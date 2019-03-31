package superstartrek.client.pwa;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;

import superstartrek.client.Application;
import superstartrek.client.pwa.ApplicationUpdateEvent.Status;

public class PWA {

	AppInstallationEvent deferredInstallationPrompt;
	Application application;
	
	private static Logger log = Logger.getLogger("");

	public static native boolean supportsServiceWorker() /*-{
															return navigator.serviceWorker!=null;
															}-*/;

	public static native void registerServiceWorker(String url) /*-{
																navigator.serviceWorker.register(url, {scope:'.'})
																.then(function(arg){
																console.log("REGISTERED SW");
																return null;
																})
																['catch'](console.error);
																}-*/;
	public static native void checkIfServiceWorkerIsRegistered(String url, RequestCallback callback)/*-{
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
	}
	
	public void installApplication() {
		deferredInstallationPrompt.prompt();
		deferredInstallationPrompt = null;
	}
	
	public PWA(Application application) {
		this.application = application;
	}

	public void dismissInstallationPrompt() {
		deferredInstallationPrompt = null;
	}
	
	public void getChecksumOfInstalledApplication(RequestCallback callback) {
		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, "/superstartrek/site/superstartrek.superstartrek.nocache.js.md5");
		try {
			rb.sendRequest("", callback);
		} catch (RequestException e) {
			GWT.log(e.getMessage(),e);
		}
	}

	public void getChecksumOfNewestVersion(RequestCallback callback) {
		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, "/superstartrek/site/superstartrek.superstartrek.nocache.js.md5?rnd="+Random.nextInt());
		try {
			rb.sendRequest("", callback);
		} catch (RequestException e) {
			GWT.log(e.getMessage(),e);
		}
	}
	
	public void checkForNewVersion(Application app) {
		
		log.info("check for new version");
		getChecksumOfInstalledApplication(new RequestCallback() {
			
			@Override
			public void onResponseReceived(Request request, Response response) {
				String checksumOfInstalledApplication = response.getText();
				log.info("installed version id "+checksumOfInstalledApplication);
				getChecksumOfNewestVersion(new RequestCallback() {
					
					@Override
					public void onResponseReceived(Request request, Response response) {
						String checksumOfNewestVersion = response.getText();
						log.info("newest version id "+checksumOfNewestVersion);
						boolean isSame = checksumOfInstalledApplication.equals(checksumOfNewestVersion);
						app.events.fireEvent(new ApplicationUpdateEvent(isSame?Status.appIsUpToDate:Status.appIsOutdated));
						
					}
					
					@Override
					public void onError(Request request, Throwable exception) {
						app.events.fireEvent(new ApplicationUpdateEvent(Status.checkFailed));
					}
				});
			}
			
			@Override
			public void onError(Request request, Throwable exception) {
				app.events.fireEvent(new ApplicationUpdateEvent(Status.checkFailed));
			}
		});
	}

	public void run() {
		if (!GWT.isClient())
			return;
		if (!supportsServiceWorker())
			return;
		registerServiceWorker("service-worker.js");
		addInstallationListener();
		checkForNewVersion(application);
	}
}
