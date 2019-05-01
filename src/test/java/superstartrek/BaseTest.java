package superstartrek;

import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Before;

import com.google.gwt.event.shared.testing.CountingEventBus;

import superstartrek.client.Application;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;
import superstartrek.client.utils.BrowserAPI;

public abstract class BaseTest {

	Application application;
	StarMap starMap;
	CountingEventBus events;
	BrowserAPI browser;
	Enterprise enterprise;
	Quadrant quadrant;
	
	@Before
	public void setupCommonObjects() {
		application = new Application();
		starMap = new StarMap();
		application.browserAPI = browser = mock(BrowserAPI.class);
		application.events = events = new CountingEventBus();
		application.starMap = starMap;
		starMap.enterprise = enterprise = new Enterprise(application, starMap);
		quadrant = new Quadrant("test quadrant 1:2", 1, 2);
		starMap.setQuadrant(quadrant);
		enterprise.setQuadrant(quadrant);
	}
	
	@After
	public void cleanUp() {
		Application.set(null);
	}
}
