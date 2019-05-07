package superstartrek.client.activities.pwa;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;

import superstartrek.client.Application;
import superstartrek.client.activities.pwa.ApplicationLifecycleHandler.ApplicationLifecycleEvent;
import superstartrek.client.activities.pwa.ApplicationLifecycleHandler.ApplicationLifecycleEvent.Status;
import superstartrek.client.activities.pwa.http.RequestFactory;
import superstartrek.client.activities.pwa.localcache.LocalCache;
import superstartrek.client.activities.pwa.localcache.LocalCacheBrowserImpl;
import superstartrek.client.activities.pwa.promise.Promise;

public class PWA {

	final static String CACHE_NAME = "sst1";
	AppInstallationEvent deferredInstallationPrompt;
	Application application;
	LocalCache cache;
	RequestFactory requestFactory;

	private static Logger log = Logger.getLogger("");

	public void setRequestFactory(RequestFactory rf) {
		this.requestFactory = rf;
	}

	public void setCacheImplementation(LocalCache cache) {
		this.cache = cache;
	}

	public void clearCache(ScheduledCommand callback) {
		cache.clearCache(CACHE_NAME, application.requestFactory, callback);
	}

	//@formatter:off
	public static native boolean supportsServiceWorker() /*-{
		return $wnd.navigator.serviceWorker!=null;
	}-*/;

	public static native void log(Throwable t) /*-{
		console.log(t.message, t);
	}-*/;

	/*
	 * Tricky thing to remember: if the user dismisses the native installation
	 * prompt, the "beforeinstallprompt" event fires again!
	 */
	public native void addInstallationListener() /*-{
		var that = this;
		$wnd.addEventListener('beforeinstallprompt', function (e){
			that.@superstartrek.client.activities.pwa.PWA::installationEventCallback(Lsuperstartrek/client/activities/pwa/AppInstallationEvent;)(e);
		});
	}-*/;
	//@formatter:on

	public void installationEventCallback(AppInstallationEvent e) {
		log.info("installation event callback");
		deferredInstallationPrompt = e;
		deferredInstallationPrompt.preventDefault();
		application.events.fireEvent(new ApplicationLifecycleEvent(Status.showInstallPrompt, "", "", ""));
	}

	public void installApplication() {
		log.info("invoking deferred installation prompt");
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
		superstartrek.client.activities.pwa.http.Request r = requestFactory.create();
		try {
			r.request(RequestBuilder.GET, "/superstartrek/site/checksum.sha.md5", callback);
		} catch (Exception e) {
			log.info(e.getMessage());
		}
	}

	public void getChecksumOfNewestVersion(RequestCallback callback) {
		superstartrek.client.activities.pwa.http.Request r = requestFactory.create();
		int rnd = application.browserAPI.nextInt(100000);
		try {
			r.request(RequestBuilder.GET, "/superstartrek/site/checksum.sha.md5?rnd=" + rnd, callback);
		} catch (Exception e) {
			log.info(e.getMessage());
		}
	}

	public void checkForNewVersion() {
		log.info("Checking for new version");
		Application app = application;
		getChecksumOfInstalledApplication(new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {
				String checksumOfInstalledApplication = response.getText();
				String dateOfInstalledApplication = response.getHeader("last-modified");
				log.info("Installed app version " + checksumOfInstalledApplication);
				application.events.fireEvent(new ApplicationLifecycleEvent(Status.informingOfInstalledVersion,
						checksumOfInstalledApplication, dateOfInstalledApplication, ""));
				getChecksumOfNewestVersion(new RequestCallback() {

					@Override
					public void onResponseReceived(Request request, Response response) {
						String checksumOfNewestVersion = response.getText();
						log.info("Checksum of latest package : " + checksumOfNewestVersion);
						boolean isSame = checksumOfInstalledApplication.equals(checksumOfNewestVersion);
						log.info("is same: " + isSame);
						if (response.getStatusCode() != 200 && response.getStatusCode() != 304) {
							GWT.log("update check failed");
							app.events.fireEvent(new ApplicationLifecycleEvent(Status.checkFailed,
									checksumOfInstalledApplication, dateOfInstalledApplication, ""));
							return;
						}
						app.events.fireEvent(
								new ApplicationLifecycleEvent(isSame ? Status.appIsUpToDate : Status.appIsOutdated,
										checksumOfInstalledApplication, dateOfInstalledApplication, checksumOfNewestVersion));
					}

					@Override
					public void onError(Request request, Throwable exception) {
						app.events.fireEvent(
								new ApplicationLifecycleEvent(Status.checkFailed, dateOfInstalledApplication, checksumOfInstalledApplication, ""));
					}
				});
			}

			@Override
			public void onError(Request request, Throwable exception) {
				app.events.fireEvent(new ApplicationLifecycleEvent(Status.checkFailed, "", "", ""));
			}
		});
	}

	public void setupCache() {
		if (cache == null)
			cache = LocalCacheBrowserImpl.getInstance();
	}

	public void run() {
		if (!GWT.isClient()) {
			log.info("Not running PWA because not running in browser");
			return;
		}
		addInstallationListener();
		setupCache();
		checkForNewVersion();
	}
}
