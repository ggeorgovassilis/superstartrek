package superstartrek.client.activities.pwa.localcache;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LocalCache {

	Void queryCacheExistence(String name, AsyncCallback<Boolean> callback);
	Void cacheFiles(String name, String[] urls , ScheduledCommand callback);
	Void clearCache(String cacheNameToDelete, ScheduledCommand callback);

}
