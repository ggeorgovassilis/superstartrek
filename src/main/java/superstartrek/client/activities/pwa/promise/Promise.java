package superstartrek.client.activities.pwa.promise;

import com.google.gwt.core.client.JavaScriptObject;

import superstartrek.client.activities.pwa.Callback;

public interface Promise<T> {

	Promise<T> then(Callback<T> callback);
	Promise<JavaScriptObject[]> all(Promise<?>[] promises);
}
