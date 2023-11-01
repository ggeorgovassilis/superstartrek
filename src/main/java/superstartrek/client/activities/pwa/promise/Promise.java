package superstartrek.client.activities.pwa.promise;

import superstartrek.client.activities.pwa.Callback;

public interface Promise<T> {

	Promise<T> then(Callback<T> callback);
	Promise<T>[] all(Promise<T>[] promises);
}
