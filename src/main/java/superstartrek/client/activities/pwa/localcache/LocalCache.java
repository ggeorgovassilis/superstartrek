package superstartrek.client.activities.pwa.localcache;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import superstartrek.client.activities.pwa.Callback;
import superstartrek.client.activities.pwa.http.RequestFactory;
import superstartrek.client.activities.pwa.promise.Promise;

public interface LocalCache {

	Promise<Boolean> queryCacheExistence(String name);
	Void cacheFiles(String name, String[] urls, Callback<JavaScriptObject> callback);
	Void clearCache(String cacheNameToDelete, RequestFactory rf, ScheduledCommand callback);

}
