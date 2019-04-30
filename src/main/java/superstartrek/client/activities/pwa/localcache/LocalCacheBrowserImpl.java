package superstartrek.client.activities.pwa.localcache;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class LocalCacheBrowserImpl extends JavaScriptObject implements LocalCache {

	protected LocalCacheBrowserImpl() {
	}
	
	//@formatter:off
	public static native LocalCacheBrowserImpl getInstance()/*-{
		return $wnd.caches;
	}-*/;

	
	public final native Void queryCacheExistence(String name, AsyncCallback<Boolean> callback)/*-{
	this.has(name).then(function(hasCache) {
    	callback.@com.google.gwt.user.client.rpc.AsyncCallback::onSuccess(Ljava/lang/Object;)(hasCache);
	});
	}-*/;

	// caching in the main window is possible according to
	// https://gist.github.com/Rich-Harris/fd6c3c73e6e707e312d7c5d7d0f3b2f9
	public final native Void cacheFiles(String cacheName, String[] files, ScheduledCommand callback) /*-{
	this.has(cacheName).then(function(hasCache) {
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
