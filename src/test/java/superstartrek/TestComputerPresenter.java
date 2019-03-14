package superstartrek;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import com.google.gwt.event.shared.testing.CountingEventBus;

import superstartrek.client.Application;
import superstartrek.client.activities.computer.ComputerPresenter;
import superstartrek.client.activities.computer.IComputerView;
import superstartrek.client.activities.computer.TurnStartedEvent;
import superstartrek.client.activities.navigation.EnterpriseRepairedEvent;
import superstartrek.client.activities.navigation.ThingMovedEvent;
import superstartrek.client.activities.navigation.ThingMovedHandler;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarBase;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;


public class TestComputerPresenter {

	Application app;
	CountingEventBus events;
	StarMap map;
	Quadrant quadrant;
	Enterprise enterprise;
	ComputerPresenter presenter;
	IComputerView view;

	@Before
	public void setup() {
		app  = Application.get();
		app.events = events = new CountingEventBus();
		
		quadrant = new Quadrant("test", 1,2);
		map = new StarMap();
		map.setQuadrant(quadrant);
		app.starMap = map;
		
		enterprise = new Enterprise(app);
		enterprise.setQuadrant(quadrant);
		map.enterprise = enterprise;
		
		presenter = new ComputerPresenter(app);
		view = mock(IComputerView.class);
		presenter.setView(view);
	}

	@Test
	public void testOnTurnStarted_1() {
		TurnStartedEvent evt = new TurnStartedEvent();
		presenter.onTurnStarted(evt);
		
		verify(view).setDockInStarbaseButtonVisibility(false);
		verify(view).setRepairButtonVisibility(false);
		verify(view).updateShortStatus(eq(""), eq(""), eq(""), eq(""));
	}

	@Test
	public void testOnTurnStarted_2() {
		enterprise.setLocation(Location.location(1,1));
		enterprise.getPhasers().damage(10);
		quadrant.setStarBase(new StarBase(Location.location(3,3)));
		TurnStartedEvent evt = new TurnStartedEvent();
		presenter.onTurnStarted(evt);

		
		verify(view).setDockInStarbaseButtonVisibility(true);
		verify(view).setRepairButtonVisibility(false);
		verify(view).updateShortStatus(eq(""), eq(""), eq("damage-light"), eq(""));
	}
	
	@Test
	public void testDockWithStarbase() {
		enterprise.setLocation(Location.location(1,1));
		enterprise.getPhasers().damage(10);
		enterprise.getAntimatter().decrease(10);
		enterprise.getTorpedos().damage(1);
		enterprise.getImpulse().damage(1);
		quadrant.setStarBase(new StarBase(Location.location(3,3)));
		
		events.addHandler(ThingMovedEvent.TYPE, new ThingMovedHandler() {
			
			@Override
			public void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
				assertEquals(enterprise, thing);
				assertEquals(quadrant, qFrom);
				assertEquals(Location.location(1,1), lFrom);
				assertEquals(quadrant, qTo);
				assertEquals(Location.location(2,2), lTo);
			}
		});
		
		presenter.onDockInStarbaseButtonClicked();
		
		assertEquals(1, events.getFiredCount(ThingMovedEvent.TYPE));
		assertEquals(Location.location(2,2), enterprise.getLocation());
		
		assertEquals(1, events.getFiredCount(EnterpriseRepairedEvent.TYPE));
		assertEquals(enterprise.getTorpedos().getMaximum(), enterprise.getTorpedos().getValue(), 0.1);
	}

	
}
