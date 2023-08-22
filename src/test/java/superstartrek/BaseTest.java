package superstartrek;

import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Before;

import eventbus.CountingEventBus;
import superstartrek.client.Application;
import superstartrek.client.space.Quadrant;
import superstartrek.client.space.StarMap;
import superstartrek.client.utils.BrowserAPI;
import superstartrek.client.vessels.Enterprise;

public abstract class BaseTest {

	Application application;
	StarMap starMap;
	CountingEventBus events;
	BrowserAPI browser;
	Enterprise enterprise;
	Quadrant quadrant;
	CountingEventBus bus;
	
	@Before
	public void setupCommonObjects() {
		application = new Application();
		bus = new CountingEventBus();
		application.eventBus = bus;
		starMap = new StarMap();
		application.browserAPI = browser = mock(BrowserAPI.class);
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
