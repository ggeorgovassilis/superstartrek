package superstartrek.client.activities.pwa.http;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;

public interface Request {

	Void request(RequestBuilder.Method method, String url, RequestCallback callback);
}
