package superstartrek.client.activities.pwa.http;

import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.gwt.user.client.Window;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

public class RequestBrowserImpl implements Request{

	@Override
	public Void request(Method method, String url, RequestCallback callback) {
		RequestBuilder rb = new RequestBuilder(method, url);
		rb.setCallback(new RequestCallback() {
			
			@Override
			public void onResponseReceived(com.google.gwt.http.client.Request request, Response response) {
				if (response.getStatusCode()==500)
					callback.onError(request, new Exception("Service worker responded with error"));
				else
				callback.onResponseReceived(request, response);
			}
			
			@Override
			public void onError(com.google.gwt.http.client.Request request, Throwable exception) {
				callback.onError(request, exception);
			}
		});
		try {
			rb.send();
		} catch (Throwable e) {
			Window.alert("oops");
			callback.onError(null, e);
		}
		return null;
	}

}
