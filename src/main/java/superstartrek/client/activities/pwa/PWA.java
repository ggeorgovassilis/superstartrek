package superstartrek.client.activities.pwa;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Random;
import superstartrek.client.Application;
import superstartrek.client.activities.pwa.ApplicationUpdateCheckHandler.ApplicationUpdateEvent;
import superstartrek.client.activities.pwa.ApplicationUpdateCheckHandler.ApplicationUpdateEvent.Status;

public class PWA {

	AppInstallationEvent deferredInstallationPrompt;
	Application application;

	private static Logger log = Logger.getLogger("");

	private String[] URLS = { "/superstartrek/site/index.html", "/superstartrek/site/images/cancel.svg",
			"/superstartrek/site/images/communicator.svg", "/superstartrek/site/images/federation_logo.svg",
			"/superstartrek/site/images/fire_at_will.svg", "/superstartrek/site/images/hexagon_filled.svg",
			"/superstartrek/site/images/hexagon.svg", "/superstartrek/site/images/icon192x192.png",
			"/superstartrek/site/images/icon512x512.png", "/superstartrek/site/images/laser.svg",
			"/superstartrek/site/images/navigation.svg", "/superstartrek/site/images/radar.svg",
			"/superstartrek/site/images/stars-background.gif", "/superstartrek/site/images/torpedo.svg",
			"/superstartrek/site/css/sst.css", "/superstartrek/site/superstartrek.superstartrek.nocache.js",
			"/superstartrek/site/checksum.sha.md5", };

	// caching in the main window is possible according to
	// https://gist.github.com/Rich-Harris/fd6c3c73e6e707e312d7c5d7d0f3b2f9
	//@formatter:off
	private static native void cacheFiles(String[] files) /*-{
		$wnd.caches.open( "sst1" )
    	.then( function(cache){cache.addAll( files )} )
    	.then( function(){console.log( 'offline cache populated' );} )
    	["catch"]( function(){console.log('something went wrong')} );
	}-*/;
	//@formatter:on

	//@formatter:off
	public static native void clearCache(ScheduledCommand callback)/*-{
	$wnd.caches.keys().then(function(cacheNames) {return Promise.all(
        cacheNames.filter(function(cacheName) {
        	console.log('SW','clearing',cacheName);
        	return true;
        }).map(function(cacheName) {
          return caches['delete'](cacheName).then(function(){
          console.log("Caches are deleted");
		  callback.@com.google.gwt.core.client.Scheduler.ScheduledCommand::execute()();
          });
        })
      );
    });
	}-*/;
	//@formatter:on

	public void cacheFilesForOfflineUse() {
		GWT.log("Caching files for offline use");
		cacheFiles(URLS);
	}

	//@formatter:off
	public static native boolean supportsServiceWorker() /*-{
		return navigator.serviceWorker!=null;
	}-*/;
	//@formatter:on

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

	public void checkForNewVersion() {
		Application app = application;
		getChecksumOfInstalledApplication(new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {
				String checksumOfInstalledApplication = response.getText();
				application.events.fireEvent(new ApplicationUpdateEvent(Status.informingOfInstalledVersion,
						checksumOfInstalledApplication, ""));
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
						app.events.fireEvent(
								new ApplicationUpdateEvent(Status.checkFailed, checksumOfInstalledApplication, ""));
					}
				});
			}

			@Override
			public void onError(Request request, Throwable exception) {
				app.events.fireEvent(new ApplicationUpdateEvent(Status.checkFailed, "", ""));
			}
		});
	}

	public void run() {
		if (!GWT.isClient() || !GWT.isProdMode())
			return;
		if (!supportsServiceWorker())
			return;
		cacheFilesForOfflineUse();
		registerServiceWorker("service-worker.js");
		addInstallationListener();
		checkForNewVersion();
	}
}
