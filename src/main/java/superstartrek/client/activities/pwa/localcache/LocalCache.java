package superstartrek.client.activities.pwa.localcache;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

import superstartrek.client.activities.pwa.promise.Promise;

public interface LocalCache {

	Promise<Boolean> queryCacheExistence(String name);
	Void cacheFiles(String name, String[] urls, ScheduledCommand callback);
	Void clearCache(String cacheNameToDelete, ScheduledCommand callback);

}
