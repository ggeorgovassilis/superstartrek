package superstartrek.client.activities.pwa.localcache;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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
		console.log("has cache ",name);
		return this.has(name);
	}-*/;

	private final native PromiseBrowserImpl<JsCache> open(String name)/*-{
		console.log("opening cache ",name);
		return this.open(name);
	}-*/;

	
	public void log(String name) {
		if (GWT.isClient())
			log(name);
	}
	
	private final native void _log(String name)/*-{
	console.log(name);
	}-*/;

	//@formatter:on
	@Override
	public final Void cacheFiles(String cacheName, String[] files, Callback<Void> callback) {
		log("cacheFiles");
		has(cacheName).then((hasCache) -> {
			log("hasCache " + hasCache);
			if (hasCache)
				return;
			log("Proceeding to cache files");
			open(cacheName).then((jsCache) -> {
				log("openend cache, adding files");
				jsCache.addAll(files);
			}).then((result) -> {
				log("added files, doing callback");
				callback.onSuccess(null);
			});
		});
		return null;
	}

	@Override
	// TODO: rewrite in java
	//@formatter:off
	public final native Void clearCache(String cacheNameToDelete, ScheduledCommand callback)/*-{
		console.log("asked to empty cache "+cacheNameToDelete);
		this.keys().then(function(cacheNames) {
		console.log("looking at ",cacheNames);
			return Promise.all(cacheNames.filter(function(cacheName) {
				console.log("looking at ",cacheName);
					return cacheName == cacheNameToDelete;
				}).map(function(cacheName) {
				console.log("deleting ",cacheName);
				return caches['delete'](cacheName).then(function(){
					console.log("Caches are deleted");
					callback.@com.google.gwt.core.client.Scheduler.ScheduledCommand::execute()();
				});
			}));
		});
	}-*/;
	//@formatter:on

}
