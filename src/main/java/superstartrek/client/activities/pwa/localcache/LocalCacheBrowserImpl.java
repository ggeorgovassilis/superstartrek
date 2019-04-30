package superstartrek.client.activities.pwa.localcache;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import superstartrek.client.activities.pwa.Callback;
import superstartrek.client.activities.pwa.promise.Promise;
import superstartrek.client.activities.pwa.promise.PromiseBrowserImpl;

public class LocalCacheBrowserImpl extends JavaScriptObject implements LocalCache {

	private static Logger log = Logger.getLogger("");

	protected LocalCacheBrowserImpl() {
	}
	
	//@formatter:off
	public static native LocalCacheBrowserImpl getInstance()/*-{
		return $wnd.caches;
	}-*/;


	@Override
	public final Promise<Boolean> queryCacheExistence(String name){
		return has(name);
	}

	private final native PromiseBrowserImpl<Boolean> has(String name)/*-{
		return this.has(name);
	}-*/;

	private final native PromiseBrowserImpl<JsCache> open(String name)/*-{
		return this.open(name);
	}-*/;

	public final Void cacheFiles(String cacheName, String[] files, Callback<Void> callback) {
		GWT.log("cacheFiles");
		has(cacheName).then(new Callback<Boolean>() {

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(Boolean hasCache) {
				if (hasCache)
					return;
				GWT.log("Proceeding to cache files");
				open(cacheName).then(new Callback<JsCache>() {

					@Override
					public void onSuccess(JsCache jsCache) {
						jsCache.addAll(files);
					}
				}).then(new AsyncCallback<JsCache>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(JsCache result) {
						GWT.log("Success!");
						callback.onSuccess(null);
					}
				});
			}
		});
		return null;
	}

	// caching in the main window is possible according to
	// https://gist.github.com/Rich-Harris/fd6c3c73e6e707e312d7c5d7d0f3b2f9
	private final native Void _cacheFiles(String cacheName, String[] files, ScheduledCommand callback) /*-{
			$wnd.caches.open( "sst1" )
    			.then( function(cache){cache.addAll( files )} )
    			.then( function(){
    			console.log( 'Offline cache populated.' );
    			callback.@com.google.gwt.core.client.Scheduler.ScheduledCommand::execute()();
    			})
    		["catch"]( function(){console.log('something went wrong')} );
	}-*/;
	
	public final native Void clearCache(String cacheNameToDelete, ScheduledCommand callback)/*-{
	this.keys().then(function(cacheNames) {return Promise.all(
        cacheNames.filter(function(cacheName) {
        	return cacheName == cacheNameToDelete;
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

}
