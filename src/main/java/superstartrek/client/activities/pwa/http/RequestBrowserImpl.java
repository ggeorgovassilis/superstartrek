package superstartrek.client.activities.pwa.http;

import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;

public class RequestBrowserImpl implements Request{

	@Override
	public Void request(Method method, String url, RequestCallback callback) {
		RequestBuilder rb = new RequestBuilder(method, url);
		rb.setCallback(callback);
		try {
			rb.send();
		} catch (RequestException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

}
