package superstartrek.client.activities.pwa.localcache;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import superstartrek.client.activities.pwa.Callback;
import superstartrek.client.activities.pwa.http.RequestFactory;
import superstartrek.client.activities.pwa.promise.Promise;

public class CacheNOPImpl implements LocalCache{
	
	Map<String, String> files = new HashMap<String, String>();

	@Override
	public Promise<Boolean> queryCacheExistence(String name) {
		return new Promise<Boolean>() {
			
			@Override
			public Promise<Boolean> then(Callback<Boolean> callback) {
				callback.onSuccess(false);
				return this;
			}
			
			@Override
			public Promise<Boolean>[] all(Promise<Boolean>[] promises) {
				return promises;
			}
		};
	}

	@Override
	public Void cacheFiles(String name, String[] urls, Callback<JavaScriptObject> callback) {
		int outstandingRequests[] = {urls.length};
		for (String url:urls) {
			RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, url);
			try {
				RequestCallback requestCallback = new RequestCallback() {
					@Override
					public void onResponseReceived(Request request, Response response) {
						files.put(url, response.getText());
						outstandingRequests[0]--;
						if (outstandingRequests[0]==0)
							callback.onSuccess(null);
					}
					
					@Override
					public void onError(Request request, Throwable exception) {
						GWT.log("Server return error requesting "+url);
						callback.onFailure(exception);
					}
				};
				
				rb.sendRequest(null, requestCallback);

			} catch (RequestException e) {
				GWT.log("Error requesting "+url, e);
				callback.onFailure(e);
			}
		}
		return null;
	}

	@Override
	public Void clearCache(String cacheNameToDelete, RequestFactory rf, ScheduledCommand callback) {
		return null;
	}


}
