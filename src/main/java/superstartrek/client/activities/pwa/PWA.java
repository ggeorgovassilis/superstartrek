package superstartrek.client.activities.pwa;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import superstartrek.client.Application;
import superstartrek.client.activities.pwa.localcache.LocalCache;
import superstartrek.client.activities.pwa.localcache.LocalCacheBrowserImpl;
import superstartrek.client.eventbus.Events;

public class PWA {

	final String CHECKSUM_URL = "/superstartrek/site/package.txt";

	final String CACHE_NAME = "sst1";
	AppInstallationEvent deferredInstallationPrompt;
	Application application;
	LocalCache cache;

	private static Logger log = Logger.getLogger("");

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
	public native void addInstallationListener(Callback<AppInstallationEvent> callback) /*-{
	$wnd.addEventListener('beforeinstallprompt', function (e){
	callback.@superstartrek.client.activities.pwa.Callback::onSuccess(Ljava/lang/Object;)(e);
	});
	}-*/;
	//@formatter:on

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

	public void getFileContent(String url, Callback<String> callback) {
		superstartrek.client.activities.pwa.http.Request r = application.requestFactory.create();
		r.request(RequestBuilder.GET, url, new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() == 200 || response.getStatusCode() == 304) {
					String responseText = response.getText();
					callback.onSuccess(responseText);
				} else 
					onError(request, new Exception("Wrong response code " + response.getStatusCode()));
			}

			@Override
			public void onError(Request request, Throwable exception) {
				log.severe("getFileContent.onError: " + exception.getMessage());
				callback.onFailure(exception);
			}
		});
	}

	public void getLatestVersionFromServer(Callback<String> callback) {
		//append random string to URL to bypass potential caching
		getFileContent(CHECKSUM_URL + "?rnd=" + application.browserAPI.nextInt(10000), new Callback<String>() {

			@Override
			public void onSuccess(String result) {
				String checksumOfNewestVersion = result;
				log.info("Checksum of latest package : " + checksumOfNewestVersion);
				callback.onSuccess(checksumOfNewestVersion);
			}

			@Override
			public void onFailure(Throwable caught) {
				application.eventBus.fireEvent(Events.VERSION_CHECK_FAILED, (h) -> h.checkFailed());

			}
		});
	}

	String getBuildNumber() {
		return application.browserAPI.getAppBuildNr();
	}

	public void checkForNewVersion() {
		log.info("Checking for new version");
		Application app = application;
		String currentBuildNr = app.browserAPI.getAppBuildNr();
		log.info("Installed app version " + currentBuildNr);
		application.eventBus.fireEvent(Events.INFORMING_OF_INSTALLED_VERSION,
				(h) -> h.installedAppVersionIs(currentBuildNr));
		getLatestVersionFromServer((latestBuildVersion) -> {
			boolean isSame = currentBuildNr.equals(latestBuildVersion);
			log.info("is same: " + isSame);
			if (isSame)
				app.eventBus.fireEvent(Events.VERSION_IS_CURRENT, (h) -> h.versionIsCurrent(currentBuildNr));
			else
				app.eventBus.fireEvent(Events.NEW_VERSION_AVAILABLE, (h) -> h.newVersionAvailable(currentBuildNr, latestBuildVersion));
		});

	}

	public void setupCache(Callback<Void> callback) {
		if (cache == null) {
			cache = LocalCacheBrowserImpl.getInstance();
			if (cache == null)
				throw new RuntimeException("No cache implementation found. maybe you are visiting this site over http and it isn't localhost?");
			setCacheImplementation(cache);
		}
		log.info("Querying cache existence");
		cache.queryCacheExistence(CACHE_NAME).then((exists) -> {

			if (!exists) {
				log.info("Cache does not exist");
				cache.cacheFiles(CACHE_NAME, FilesToCache.fileNames, callback);
			} else {
				log.info("Cache exists");
				callback.onSuccess(null);
			}
		});
	}

	public void run(Callback<Void> callback) {
		if (!GWT.isClient()) {
			log.info("Not running PWA because not running in browser");
			return;
		}
		addInstallationListener((event) -> {
			log.info("installation event callback");
			deferredInstallationPrompt = event;
			deferredInstallationPrompt.preventDefault();
			application.eventBus.fireEvent(Events.SHOW_APP_INSTALL_PROMPT, (h) -> h.showInstallPrompt());
		});
		setupCache(callback);
	}
}
