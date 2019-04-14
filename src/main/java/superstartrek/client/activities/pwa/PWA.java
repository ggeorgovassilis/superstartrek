package superstartrek.client.activities.pwa;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Random;
import superstartrek.client.Application;
import superstartrek.client.utils.Timer;
import superstartrek.client.activities.pwa.ApplicationUpdateCheckHandler.ApplicationUpdateEvent;
import superstartrek.client.activities.pwa.ApplicationUpdateCheckHandler.ApplicationUpdateEvent.Status;

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
																})['catch'](function(e){console.error(e.message)});
																}-*/;

	public static native void checkIfServiceWorkerIsRegistered(String url, RequestCallback callback)/*-{
																									}-*/;

	/*
	 * Tricky thing to remember: if the user dismisses the native installation
	 * prompt, the "beforeinstallprompt" event fires again!
	 */
	public native void addInstallationListener() /*-{
													var that = this;
													$wnd.addEventListener('beforeinstallprompt', function (e){
													console.log("beforeinstallprompt");
													that.@superstartrek.client.activities.pwa.PWA::installationEventCallback(Lsuperstartrek/client/activities/pwa/AppInstallationEvent;)(e);
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
		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, "/superstartrek/site/checksum.sha.md5");
		try {
			rb.sendRequest("", callback);
		} catch (RequestException e) {
			GWT.log(e.getMessage(), e);
		}
	}

	public void getChecksumOfNewestVersion(RequestCallback callback) {
		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET,
				"/superstartrek/site/checksum.sha.md5?rnd=" + Random.nextInt());
		try {
			rb.sendRequest("", callback);
		} catch (RequestException e) {
			GWT.log(e.getMessage(), e);
		}
	}

	public void clearCache(ScheduledCommand callback) {
		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET,
				"/superstartrek/site/checksum.sha.md5?__purge_cache");
		try {
			rb.sendRequest("", new RequestCallback() {

				@Override
				public void onResponseReceived(Request request, Response response) {
				}

				@Override
				public void onError(Request request, Throwable exception) {
				}
			});
			// TODO: super-bad hack: there is currently no callback when the cache has been
			// cleaned.
			// this heuristic assumes that the cache has been cleared in 2 seconds
			Timer.postpone(new RepeatingCommand() {

				@Override
				public boolean execute() {
					callback.execute();
					return false;
				}
			}, 2000);
		} catch (RequestException e) {
			GWT.log(e.getMessage(), e);
		}

	}

	public void checkForNewVersion() {
		Application app = application;
		getChecksumOfInstalledApplication(new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {
				String checksumOfInstalledApplication = response.getText();
				application.events.fireEvent(new ApplicationUpdateEvent(Status.informingOfInstalledVersion, checksumOfInstalledApplication, ""));
				getChecksumOfNewestVersion(new RequestCallback() {

					@Override
					public void onResponseReceived(Request request, Response response) {
						log.info("Checksum of installed package : " + checksumOfInstalledApplication);
						String checksumOfNewestVersion = response.getText();
						log.info("Checksum of latest    package : " + checksumOfNewestVersion);
						if (response.getStatusCode() != 200 && response.getStatusCode() != 304) {
							app.events.fireEvent(
									new ApplicationUpdateEvent(Status.checkFailed, checksumOfInstalledApplication, ""));
							return;
						}
						boolean isSame = checksumOfInstalledApplication.equals(checksumOfNewestVersion);
						app.events.fireEvent(
								new ApplicationUpdateEvent(isSame ? Status.appIsUpToDate : Status.appIsOutdated,
								checksumOfInstalledApplication, checksumOfNewestVersion));

					}

					@Override
					public void onError(Request request, Throwable exception) {
						app.events.fireEvent(new ApplicationUpdateEvent(Status.checkFailed, checksumOfInstalledApplication, ""));
					}
				});
			}

			@Override
			public void onError(Request request, Throwable exception) {
				app.events.fireEvent(new ApplicationUpdateEvent(Status.checkFailed, "", ""));
			}
		});
	}

	public void queryAppVersion(ValueChangeHandler<String> callback) {

	}

	public void run() {
//		if (!GWT.isClient() || !GWT.isProdMode())
//			return;
		if (!supportsServiceWorker())
			return;
		registerServiceWorker("service-worker.js");
		addInstallationListener();
		checkForNewVersion();
	}
}
