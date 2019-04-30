package superstartrek.client.activities.pwa.promise;

import com.google.gwt.user.client.rpc.AsyncCallback;

import superstartrek.client.activities.pwa.Callback;

public interface Promise<T> {

	Promise<T> then(Callback<T> callback);
	
}
