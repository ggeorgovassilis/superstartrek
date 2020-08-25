package superstartrek.client.activities.pwa;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import superstartrek.client.Application;
import superstartrek.client.activities.pwa.http.RequestFactory;
import superstartrek.client.activities.pwa.localcache.LocalCache;
import superstartrek.client.activities.pwa.localcache.LocalCacheBrowserImpl;
import superstartrek.client.bus.Events;

public class PWA {

	static String[] fileNames = new String[] {//@formatter:off
	           				".", 
	           				"index.html", 
	        				"css/sst.css", 
	        				"sst.webmanifest",
	        				"superstartrek.superstartrek.nocache.js",
	        				"images/stars-background.gif", 
	        				"service-worker.js",
	        				"sst.webmanifest",
	        				"package.txt",
	        				"images/anchor.svg",
	        				"images/bookmark.svg",
	        				"images/cancel.svg",
	        				"images/communicator.svg", 
	        				"images/federation_logo.svg",
	        				"images/hamburger-menu.svg",
	        				"images/hexagon_filled.svg",
	        				"images/hexagon.svg", 
	        				"images/icon192x192.png",
	        				"images/icon512x512.png", 
	        				"images/laser.svg",
	        				"images/navigation.svg", 
	        				"images/radar.svg",
	        				"images/repair.svg",
	        				"images/report.svg",
	        				"images/shield.svg",
	        				"images/torpedo.svg",
	        				"images/target.svg",
	        				"images/spark.svg",
	        				"images/missile.svg"
	        				//@formatter:on
	};

	final String CHECKSUM_URL = "/superstartrek/site/package.txt";

	final String CACHE_NAME = "sst1";
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

	public void getFileContent(String url, Callback<String[]> success) {
		superstartrek.client.activities.pwa.http.Request r = requestFactory.create();
		r.request(RequestBuilder.GET, url, new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() == 200 || response.getStatusCode() == 304) {
					String checksumOfInstalledApplication = response.getText();
					String dateOfInstalledApplication = response.getHeader("last-modified");
					success.onSuccess(new String[] { checksumOfInstalledApplication, dateOfInstalledApplication });
				} else
					log.severe("Wrong response code " + response.getStatusCode());
			}

			@Override
			public void onError(Request request, Throwable exception) {
				log.severe("getFileContent.onError: " + exception.getMessage());
				success.onFailure(exception);
			}
		});
	}

	public void getLatestVersionFromServer(Callback<String> callback) {
		getFileContent(CHECKSUM_URL + "?rnd=" + application.browserAPI.nextInt(10000), new Callback<String[]>() {

			@Override
			public void onSuccess(String[] result) {
				String checksumOfNewestVersion = result[0];
				log.info("Checksum of latest package : " + checksumOfNewestVersion);
				callback.onSuccess(checksumOfNewestVersion);
			}

			@Override
			public void onFailure(Throwable caught) {
				log.info("There you have it. error");
				application.eventBus.fireEvent(Events.VERSION_CHECK_FAILED, (h) -> h.checkFailed());

			}
		});
	}

	public void checkForNewVersion() {
		log.info("Checking for new version");
		Application app = application;
		getFileContent(CHECKSUM_URL, new Callback<String[]>() {

			@Override
			public void onSuccess(String[] result) {
				String checksumOfInstalledApplication = result[0];
				String dateOfInstalledApplication = result[1];
				log.info("Installed app version " + checksumOfInstalledApplication);
				application.eventBus.fireEvent(Events.INFORMING_OF_INSTALLED_VERSION,
						(h) -> h.installedAppVersionIs(checksumOfInstalledApplication, dateOfInstalledApplication));
				getLatestVersionFromServer((latestVersion) -> {
					boolean isSame = checksumOfInstalledApplication.equals(latestVersion);
					log.info("is same: " + isSame);
					if (isSame)
						app.eventBus.fireEvent(Events.VERSION_IS_CURRENT, (h) -> h.versionIsCurrent());
					else
						app.eventBus.fireEvent(Events.NEW_VERSION_AVAILABLE, (h) -> h.newVersionAvailable());
				});
			}

			@Override
			public void onFailure(Throwable caught) {
				log.severe("Update check failed");
				application.eventBus.fireEvent(Events.VERSION_CHECK_FAILED, (h) -> h.checkFailed());
			}
		});

	}

	public void setupCache(Callback<Void> callback) {
		if (cache == null)
			cache = LocalCacheBrowserImpl.getInstance();
		log.info("Querying cache existence");
		cache.queryCacheExistence(CACHE_NAME).then((exists) -> {

			if (!exists) {
				log.info("Cache does not exist");
				cache.cacheFiles(CACHE_NAME, fileNames, callback);
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
