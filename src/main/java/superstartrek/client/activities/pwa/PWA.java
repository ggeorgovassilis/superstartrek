package superstartrek.client.activities.pwa;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import superstartrek.client.Application;
import superstartrek.client.activities.pwa.ApplicationLifecycleHandler.ApplicationLifecycleEvent;
import superstartrek.client.activities.pwa.ApplicationLifecycleHandler.ApplicationLifecycleEvent.Status;
import superstartrek.client.activities.pwa.http.RequestFactory;
import superstartrek.client.activities.pwa.localcache.LocalCache;
import superstartrek.client.activities.pwa.localcache.LocalCacheBrowserImpl;

public class PWA {

	final static String CACHE_NAME = "sst1";
	AppInstallationEvent deferredInstallationPrompt;
	Application application;
	LocalCache cache;
	RequestFactory requestFactory;

	private static Logger log = Logger.getLogger("");

	//@formatter:off
	private String[] URLS = { 
			"/superstartrek/site/index.html", 
			"/superstartrek/site/images/cancel.svg",
			"/superstartrek/site/images/communicator.svg", 
			"/superstartrek/site/images/federation_logo.svg",
			"/superstartrek/site/images/fire_at_will.svg", 
			"/superstartrek/site/images/hexagon_filled.svg",
			"/superstartrek/site/images/hexagon.svg", 
			"/superstartrek/site/images/icon192x192.png",
			"/superstartrek/site/images/icon512x512.png", 
			"/superstartrek/site/images/laser.svg",
			"/superstartrek/site/images/navigation.svg", 
			"/superstartrek/site/images/radar.svg",
			"/superstartrek/site/images/torpedo.svg",
			"/superstartrek/site/images/stars-background.gif", 
			"/superstartrek/site/images/hamburger-menu.svg",
			"/superstartrek/site/css/sst.css", 
			"/superstartrek/site/superstartrek.superstartrek.nocache.js",
			"/superstartrek/site/checksum.sha.md5"
			};
	
	public void setRequestFactory(RequestFactory rf) {
		this.requestFactory = rf;
	}
	
	public void setCacheImplementation(LocalCache cache) {
		this.cache = cache;
	}
	
	public void clearCache(ScheduledCommand callback) {
		cache.clearCache(CACHE_NAME, callback);
	}

	public void cacheFilesForOfflineUse() {
		if (cache == null) {
			log.info("Cache not supported");
			return;
		}
		GWT.log("Checking for existence of cache");
		cache.queryCacheExistence(CACHE_NAME).then(new Callback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				GWT.log("Cache exists : "+result);
				if (result)
					application.events.fireEvent(new ApplicationLifecycleEvent(Status.filesCached, "", ""));
				else
					cache.cacheFiles(CACHE_NAME,URLS, new Callback<Void>() {

						@Override
						public void onSuccess(Void v) {
							application.events.fireEvent(new ApplicationLifecycleEvent(Status.filesCached, "", ""));
						}
					});
			}
		});
	}

	//@formatter:off
	public static native boolean supportsServiceWorker() /*-{
		return navigator.serviceWorker!=null;
	}-*/;
	//@formatter:on

	public static native void log(Throwable t) /*-{
												console.log(t.message, t);
												}-*/;

	//@formatter:off
	public static native void registerServiceWorker(String url) /*-{
		navigator.serviceWorker.register(url, {scope:'.'})
		.then(function(arg){
			console.log("REGISTERED SW");
			return null;
		})['catch'](function(e){console.error(e.message)});
	}-*/;
	//@formatter:on

	/*
	 * Tricky thing to remember: if the user dismisses the native installation
	 * prompt, the "beforeinstallprompt" event fires again!
	 */
	//@formatter:off
	public native void addInstallationListener() /*-{
		var that = this;
		$wnd.addEventListener('beforeinstallprompt', function (e){
			console.log("beforeinstallprompt");
			that.@superstartrek.client.activities.pwa.PWA::installationEventCallback(Lsuperstartrek/client/activities/pwa/AppInstallationEvent;)(e);
		});
	}-*/;
	//@formatter:on

	public void installationEventCallback(AppInstallationEvent e) {
		log.info("installation event callback");
		deferredInstallationPrompt = e;
		deferredInstallationPrompt.preventDefault();
		application.events.fireEvent(new ApplicationLifecycleEvent(Status.showInstallPrompt, "", ""));
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
		GWT.log("1");
		superstartrek.client.activities.pwa.http.Request r = requestFactory.create();
		GWT.log("2");
		try {
			r.request(RequestBuilder.GET, "/superstartrek/site/checksum.sha.md5", callback);
		} catch (Exception e) {
			GWT.log(e.getMessage());
		}
	}

	public void getChecksumOfNewestVersion(RequestCallback callback) {
		superstartrek.client.activities.pwa.http.Request r = requestFactory.create();
		int rnd = application.browserAPI.nextInt(100000);
		try {
			r.request(RequestBuilder.GET, "/superstartrek/site/checksum.sha.md5?rnd="+rnd, callback);
		} catch (Exception e) {
			GWT.log(e.getMessage());
		}

	}

	public void checkForNewVersion() {
		GWT.log("Checking for new version");
		Application app = application;
		getChecksumOfInstalledApplication(new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {
				String checksumOfInstalledApplication = response.getText();
				GWT.log("Installed app version "+checksumOfInstalledApplication);
				application.events.fireEvent(new ApplicationLifecycleEvent(Status.informingOfInstalledVersion,
						checksumOfInstalledApplication, ""));
				getChecksumOfNewestVersion(new RequestCallback() {

					@Override
					public void onResponseReceived(Request request, Response response) {
						log.info("Checksum of installed package : " + checksumOfInstalledApplication);
						String checksumOfNewestVersion = response.getText();
						log.info("Checksum of latest    package : " + checksumOfNewestVersion);
						if (response.getStatusCode() != 200 && response.getStatusCode() != 304) {
							app.events.fireEvent(new ApplicationLifecycleEvent(Status.checkFailed,
									checksumOfInstalledApplication, ""));
							return;
						}
						boolean isSame = checksumOfInstalledApplication.equals(checksumOfNewestVersion);
						log.info("is same: " + isSame);
						app.events.fireEvent(
								new ApplicationLifecycleEvent(isSame ? Status.appIsUpToDate : Status.appIsOutdated,
										checksumOfInstalledApplication, checksumOfNewestVersion));

					}

					@Override
					public void onError(Request request, Throwable exception) {
						app.events.fireEvent(
								new ApplicationLifecycleEvent(Status.checkFailed, checksumOfInstalledApplication, ""));
					}
				});
			}

			@Override
			public void onError(Request request, Throwable exception) {
				app.events.fireEvent(new ApplicationLifecycleEvent(Status.checkFailed, "", ""));
			}
		});
	}

	public void run() {
		if (!GWT.isClient()) {
			GWT.log("Not running PWA because not running in browser");
			return;
		}
		if (cache == null)
			cache = LocalCacheBrowserImpl.getInstance();
		if (cache != null) {
			cacheFilesForOfflineUse();
			checkForNewVersion();
		}
		if (!supportsServiceWorker()) {
			GWT.log("Not running PWA because service workers are not supported");
			return;
		}
		registerServiceWorker("service-worker.js");
		addInstallationListener();
	}
}
