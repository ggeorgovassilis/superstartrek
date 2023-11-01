package superstartrek.client.activities.pwa.localcache;

import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import superstartrek.client.activities.pwa.Callback;
import superstartrek.client.activities.pwa.http.RequestFactory;
import superstartrek.client.activities.pwa.promise.Promise;
import superstartrek.client.activities.pwa.promise.PromiseBrowserImpl;

/**
 * A wrapper around CacheStorage https://developer.mozilla.org/en-US/docs/Web/API/CacheStorage/
 */
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

	//@formatter:on
	@Override
	public final Void cacheFiles(String cacheName, String[] files, Callback<JavaScriptObject> callback) {
		log.info("Proceeding to cache files");
		@SuppressWarnings("unchecked")
		Promise<JavaScriptObject>[] promises = new Promise[files.length];
		open(cacheName).then((jsCache) -> {
			int i=0;
			for (String file:files)
				promises[i++] = jsCache.add(file);
			Promise<JavaScriptObject> p = promises[0];
			p.all(promises)[0].then((v)->{
				log.info("Finished caching all files "+v);
				callback.onSuccess(null);
			});
		});
		return null;
	}
	
	@Override
	public final Void clearCache(String cacheNameToDelete, RequestFactory rf, ScheduledCommand callback) {
		_clearCache(cacheNameToDelete, ()->{
			rf.create().request(RequestBuilder.GET, "/refresh_cache", new RequestCallback() {
				
				@Override
				public void onResponseReceived(Request request, Response response) {
					callback.execute();
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					callback.execute();
				}
			});
		});
		return null;
	}

	// TODO: rewrite in java
	//@formatter:off
	private final native Void _clearCache(String cacheNameToDelete, ScheduledCommand callback)/*-{
		console.log("clearCache1",cacheNameToDelete);
		this.keys().then(function(cacheNames) {
			console.log("clearCache2",cacheNames);
			return Promise.all(cacheNames.filter(function(cacheName) {
					console.log("clearCache3",cacheName);
					return cacheName == cacheNameToDelete;
				}).map(function(cacheName) {
				return caches['delete'](cacheName).then(function(){
					console.log("Caches are deleted");
					callback.@com.google.gwt.core.client.Scheduler.ScheduledCommand::execute()();
				});
			}));
		});
	}-*/;
	//@formatter:on

}
