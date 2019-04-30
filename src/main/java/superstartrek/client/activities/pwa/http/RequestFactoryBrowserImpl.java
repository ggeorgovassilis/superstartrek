package superstartrek.client.activities.pwa.http;

public class RequestFactoryBrowserImpl implements RequestFactory {

	@Override
	public Request create() {
		return new RequestBrowserImpl();
	}

}
