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
import com.google.gwt.user.client.rpc.AsyncCallback;

import superstartrek.client.Application;
import superstartrek.client.activities.pwa.ApplicationLifecycleHandler.ApplicationLifecycleEvent;
import superstartrek.client.activities.pwa.ApplicationLifecycleHandler.ApplicationLifecycleEvent.Status;

public class PWA {

	AppInstallationEvent deferredInstallationPrompt;
	Application application;

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
	//@formatter:on

	//@formatter:off
	public static native void checkIfCacheExists(AsyncCallback<Boolean> callback)/*-{
	$wnd.caches.has('sst1').then(function(hasCache) {
    	callback.@com.google.gwt.user.client.rpc.AsyncCallback::onSuccess(Ljava/lang/Object;)(hasCache);
	});
	}-*/;
	//@formatter:on

	// caching in the main window is possible according to
	// https://gist.github.com/Rich-Harris/fd6c3c73e6e707e312d7c5d7d0f3b2f9
	//@formatter:off
	private static native void cacheFiles(String[] files, ScheduledCommand callback) /*-{
	console.log("Caching files for offline use...");
	$wnd.caches.has('sst1').then(function(hasCache) {
  		if (!hasCache) {
  			console.log("Cache not populated yet, loading files...");
			$wnd.caches.open( "sst1" )
    			.then( function(cache){cache.addAll( files )} )
    			.then( function(){
    			console.log( 'Offline cache populated.' );
    			callback.@com.google.gwt.core.client.Scheduler.ScheduledCommand::execute()();
    			})
    		["catch"]( function(){console.log('something went wrong')} );
  		} else
			console.log("Cache already populated.");
	})["catch"](function(e) {
		console.error(e);
	});
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
		checkIfCacheExists(new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(Boolean result) {
				if (!result)
					cacheFiles(URLS, new ScheduledCommand() {

						@Override
						public void execute() {
							application.events.fireEvent(new ApplicationLifecycleEvent(Status.filesCached, "", ""));
						}
					});
					else application.events.fireEvent(new ApplicationLifecycleEvent(Status.filesCached, "", ""));
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
		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, "/superstartrek/site/checksum.sha.md5");
		try {
			rb.sendRequest("", callback);
		} catch (RequestException e) {
			log(e);
		}
	}

	public void getChecksumOfNewestVersion(RequestCallback callback) {
		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET,
				"/superstartrek/site/checksum.sha.md5?rnd=" + Random.nextInt());
		try {
			rb.sendRequest("", callback);
		} catch (RequestException e) {
			log(e);
		}
	}

	public void checkForNewVersion() {
		Application app = application;
		getChecksumOfInstalledApplication(new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {
				String checksumOfInstalledApplication = response.getText();
				application.events.fireEvent(new ApplicationLifecycleEvent(Status.informingOfInstalledVersion,
						checksumOfInstalledApplication, ""));
				getChecksumOfNewestVersion(new RequestCallback() {

					@Override
					public void onResponseReceived(Request request, Response response) {
						log.info("Checksum of installed package : " + checksumOfInstalledApplication);
						String checksumOfNewestVersion = response.getText();
						log.info("Checksum of latest    package : " + checksumOfNewestVersion);
						if (response.getStatusCode() != 200 && response.getStatusCode() != 304) {
							app.events.fireEvent(
									new ApplicationLifecycleEvent(Status.checkFailed, checksumOfInstalledApplication, ""));
							return;
						}
						boolean isSame = checksumOfInstalledApplication.equals(checksumOfNewestVersion);
						log.info("is same: "+isSame);
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
		cacheFilesForOfflineUse();
		if (!supportsServiceWorker()) {
			GWT.log("Not running PWA because service workers are not supported");
			return;
		}
		registerServiceWorker("service-worker.js");
		addInstallationListener();
		checkForNewVersion();
	}
}
