package superstartrek.client.activities.pwa.promise;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Promise<T> {

	Promise<T> then(AsyncCallback<T> callback);
	
}
