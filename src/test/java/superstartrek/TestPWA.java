package superstartrek;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.testing.CountingEventBus;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;

import superstartrek.client.Application;
import superstartrek.client.activities.pwa.ApplicationLifecycleHandler.ApplicationLifecycleEvent;
import superstartrek.client.activities.pwa.Callback;
import superstartrek.client.activities.pwa.ApplicationLifecycleHandler;
import superstartrek.client.activities.pwa.PWA;
import superstartrek.client.activities.pwa.http.Request;
import superstartrek.client.activities.pwa.http.RequestFactory;
import superstartrek.client.activities.pwa.localcache.LocalCache;
import superstartrek.client.activities.pwa.promise.Promise;
import superstartrek.client.utils.BrowserAPI;

import static org.mockito.Mockito.*;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class TestPWA {
	
	PWA pwa;
	LocalCache cache;
	Application application;
	RequestFactory requestFactory;
	CountingEventBus events;

	@Before
	public void setup() {
		application = new Application();
		pwa = new PWA(application);
		cache = mock(LocalCache.class);
		requestFactory = mock(RequestFactory.class);
		application.events = events = new CountingEventBus();
		pwa.setCacheImplementation(cache);
		pwa.setRequestFactory(requestFactory);
	}
	
	@After
	public void cleanup() {
		Application.set(null);
	}
	
	@Test
	public void test_clearCache() {
		ScheduledCommand callback = mock(ScheduledCommand.class);
		pwa.clearCache(callback);
		verify(cache).clearCache("sst1", callback);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test_cacheFilesForOfflineUse() {
		Promise<Boolean> queryPromise = mock(Promise.class);
		when(queryPromise.then(any(Callback.class))).thenAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				AsyncCallback<Boolean> callback = invocation.getArgumentAt(0, AsyncCallback.class);
				callback.onSuccess(false);
				return null;
			}
		});
		when(cache.queryCacheExistence("sst1")).thenReturn(queryPromise);
		when(cache.cacheFiles(eq("sst1"), any(String[].class), any(Callback.class))).thenAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Callback<?> c = invocation.getArgumentAt(2, Callback.class);
				c.onSuccess(null);
				return null;
			}
		});
		pwa.cacheFilesForOfflineUse();
		verify(cache).cacheFiles(eq("sst1"), any(String[].class), any(Callback.class));
	}
	
	@Test
	public void test_checkForNewVersion() {
		Request request = mock(Request.class);
		when(requestFactory.create()).thenReturn(request);
		application.browserAPI = mock(BrowserAPI.class);
		when(application.browserAPI.nextInt(any(int.class))).thenReturn(222);
		when(request.request(eq(RequestBuilder.GET), eq("/superstartrek/site/checksum.sha.md5"), any(RequestCallback.class))).then(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				RequestCallback rc = invocation.getArgumentAt(2, RequestCallback.class);
				Response response = mock(Response.class);
				when(response.getStatusCode()).thenReturn(200);
				when(response.getText()).thenReturn("12345");
				com.google.gwt.http.client.Request httpRequest = mock(com.google.gwt.http.client.Request.class);
				rc.onResponseReceived(httpRequest, response);
				return null;
			}
		});
		when(request.request(eq(RequestBuilder.GET), eq("/superstartrek/site/checksum.sha.md5?rnd=222"), any(RequestCallback.class))).then(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				RequestCallback rc = invocation.getArgumentAt(2, RequestCallback.class);
				Response response = mock(Response.class);
				when(response.getStatusCode()).thenReturn(200);
				when(response.getText()).thenReturn("12346");
				com.google.gwt.http.client.Request httpRequest = mock(com.google.gwt.http.client.Request.class);
				rc.onResponseReceived(httpRequest, response);
				return null;
			}
		});

		AtomicBoolean newVersionAvailable = new AtomicBoolean(false);
		events.addHandler(ApplicationLifecycleEvent.TYPE, new ApplicationLifecycleHandler() {
			@Override
			public void installedAppVersionIs(String version) {
				assertEquals("12345", version);
			}
			
			@Override
			public void newVersionAvailable() {
				newVersionAvailable.set(true);
			}
		});
		pwa.checkForNewVersion();
		verify(request).request(eq(RequestBuilder.GET), eq("/superstartrek/site/checksum.sha.md5"), any(RequestCallback.class));
		verify(request).request(eq(RequestBuilder.GET), eq("/superstartrek/site/checksum.sha.md5?rnd=222"), any(RequestCallback.class));
		//expect 2 events: one with current version, one with newest
		assertEquals(2, events.getFiredCount(ApplicationLifecycleEvent.TYPE));
		assertTrue(newVersionAvailable.get());
	}
	
}
