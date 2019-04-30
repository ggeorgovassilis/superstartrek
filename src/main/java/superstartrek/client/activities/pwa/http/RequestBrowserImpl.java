package superstartrek.client.activities.pwa.http;

import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;

public class RequestBrowserImpl implements Request{

	@Override
	public Void request(Method method, String url, RequestCallback callback) {
		RequestBuilder rb = new RequestBuilder(method, url);
		rb.setCallback(callback);
		return null;
	}

}
