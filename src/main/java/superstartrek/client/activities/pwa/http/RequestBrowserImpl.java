package superstartrek.client.activities.pwa.http;

import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.gwt.user.client.Window;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

public class RequestBrowserImpl implements Request{

	@Override
	public Void request(Method method, String url, RequestCallback callback) {
		RequestBuilder rb = new RequestBuilder(method, url);
		rb.setCallback(new RequestCallback() {
			
			@Override
			public void onResponseReceived(com.google.gwt.http.client.Request request, Response response) {
				int code = response.getStatusCode();
				switch(code) {
				case 200:
				case 301:
				case 304:
				case 307:
					callback.onResponseReceived(request, response);
					break;
				default:
					callback.onError(request, new Exception("Service worker responded with error"));
				}
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
