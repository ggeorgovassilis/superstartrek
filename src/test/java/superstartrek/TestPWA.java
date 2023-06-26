package superstartrek;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import superstartrek.client.activities.pwa.ApplicationLifecycleHandler;
import superstartrek.client.activities.pwa.Callback;
import superstartrek.client.activities.pwa.PWA;
import superstartrek.client.activities.pwa.http.Request;
import superstartrek.client.activities.pwa.http.RequestFactory;
import superstartrek.client.activities.pwa.localcache.LocalCache;
import superstartrek.client.activities.pwa.promise.Promise;
import superstartrek.client.bus.Events;
import superstartrek.client.utils.BrowserAPI;

import static org.mockito.Mockito.*;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class TestPWA extends BaseTest{
	
	PWA pwa;
	LocalCache cache;
	RequestFactory requestFactory;

	@Before
	public void setup() {
		pwa = new PWA(application);
		cache = mock(LocalCache.class);
		application.requestFactory = requestFactory = mock(RequestFactory.class);
		pwa.setCacheImplementation(cache);
	}
	
	@Test
	public void test_clearCache() {
		ScheduledCommand callback = mock(ScheduledCommand.class);
		pwa.clearCache(callback);
		verify(cache).clearCache(eq("sst1"), any(RequestFactory.class), eq(callback));
	}
	
	@Test
	public void test_checkForNewVersion() {
		Request request = mock(Request.class);
		when(requestFactory.create()).thenReturn(request);
		application.browserAPI = mock(BrowserAPI.class);
		when(application.browserAPI.nextInt(any(int.class))).thenReturn(222);
		when(application.browserAPI.getAppBuildNr()).thenReturn("12345");
		when(request.request(eq(RequestBuilder.GET), eq("/superstartrek/site/package.txt"), any(RequestCallback.class))).then(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				RequestCallback rc = invocation.getArgument(2, RequestCallback.class);
				Response response = mock(Response.class);
				when(response.getStatusCode()).thenReturn(200);
				when(response.getText()).thenReturn("12345");
				com.google.gwt.http.client.Request httpRequest = mock(com.google.gwt.http.client.Request.class);
				rc.onResponseReceived(httpRequest, response);
				return null;
			}
		});
		when(request.request(eq(RequestBuilder.GET), eq("/superstartrek/site/package.txt?rnd=222"), any(RequestCallback.class))).then(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				RequestCallback rc = invocation.getArgument(2, RequestCallback.class);
				Response response = mock(Response.class);
				when(response.getStatusCode()).thenReturn(200);
				when(response.getText()).thenReturn("12346");
				com.google.gwt.http.client.Request httpRequest = mock(com.google.gwt.http.client.Request.class);
				rc.onResponseReceived(httpRequest, response);
				return null;
			}
		});

		AtomicBoolean newVersionAvailable = new AtomicBoolean(false);
		bus.addHandler(Events.INFORMING_OF_INSTALLED_VERSION, new ApplicationLifecycleHandler() {
			@Override
			public void installedAppVersionIs(String version) {
				assertEquals("12345", version);
			}
		});
		bus.addHandler(Events.NEW_VERSION_AVAILABLE, new ApplicationLifecycleHandler() {
			
			@Override
			public void newVersionAvailable(String currentVersion, String newVersion) {
				newVersionAvailable.set(true);
			}
		});
		pwa.checkForNewVersion();
		verify(request).request(eq(RequestBuilder.GET), eq("/superstartrek/site/package.txt?rnd=222"), any(RequestCallback.class));
		assertEquals(1, bus.getFiredCount(Events.INFORMING_OF_INSTALLED_VERSION));
		assertEquals(1, bus.getFiredCount(Events.NEW_VERSION_AVAILABLE));
		assertTrue(newVersionAvailable.get());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void test_setupCache_success() {
		Callback<Void> callback = mock(Callback.class);
		Promise<Boolean> promise = mock(Promise.class);
		when(cache.queryCacheExistence("sst1")).thenReturn(promise);
		when(promise.then(any(Callback.class))).thenAnswer((invocation)->{
			Callback<Boolean> c = (Callback)invocation.getArguments()[0];
			c.onSuccess(true);
			return null;
		});
		pwa.setupCache(callback);
		verify(promise).then(any(Callback.class));
		verify(callback).onSuccess(null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void test_setupCache_failure() {
		Callback<Void> callback = mock(Callback.class);
		Promise<Boolean> promise = mock(Promise.class);
		when(cache.queryCacheExistence("sst1")).thenReturn(promise);
		when(promise.then(any(Callback.class))).thenAnswer((invocation)->{
			Callback<Boolean> c = (Callback)invocation.getArguments()[0];
			c.onSuccess(false);
			return null;
		});
		pwa.setupCache(callback);
		verify(promise).then(any(Callback.class));
		verify(cache).cacheFiles(eq("sst1"), any(new String[0].getClass()), eq(callback));
	}
}
