package superstartrek;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.google.gwt.event.shared.testing.CountingEventBus;

import superstartrek.client.Application;
import superstartrek.client.activities.computer.ComputerPresenter;
import superstartrek.client.activities.computer.IComputerView;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.Klingon.ShipClass;
import superstartrek.client.activities.navigation.ThingMovedHandler;
import superstartrek.client.activities.navigation.ThingMovedHandler.ThingMovedEvent;
import superstartrek.client.control.TurnStartedEvent;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarBase;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;
import superstartrek.client.utils.Random;
import superstartrek.client.activities.navigation.EnterpriseRepairedHandler.EnterpriseRepairedEvent;;

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
		Application.set(app = new Application());
		app.events = events = new CountingEventBus();

		quadrant = new Quadrant("test", 1, 2);
		map = new StarMap();
		map.setQuadrant(quadrant);
		app.starMap = map;

		enterprise = new Enterprise(app, map);
		enterprise.setQuadrant(quadrant);
		map.enterprise = enterprise;

		presenter = new ComputerPresenter(app);
		view = mock(IComputerView.class);
		presenter.setView(view);
	}
	
	@After
	public void cleanup() {
		Application.set(null);
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
		enterprise.setLocation(Location.location(1, 1));
		enterprise.getPhasers().damage(10);
		quadrant.setStarBase(new StarBase(Location.location(3, 3)));
		TurnStartedEvent evt = new TurnStartedEvent();
		presenter.onTurnStarted(evt);

		verify(view).setDockInStarbaseButtonVisibility(true);
		verify(view).setRepairButtonVisibility(false);
		verify(view).updateShortStatus(eq(""), eq(""), eq("damage-light"), eq(""));
	}

	@Test
	public void testDockWithStarbase() {
		enterprise.setLocation(Location.location(1, 1));
		enterprise.getPhasers().damage(10);
		enterprise.getAntimatter().decrease(10);
		enterprise.getTorpedos().damage(1);
		enterprise.getImpulse().damage(1);
		quadrant.setStarBase(new StarBase(Location.location(3, 3)));

		events.addHandler(ThingMovedEvent.TYPE, new ThingMovedHandler() {

			@Override
			public void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
				assertEquals(enterprise, thing);
				assertEquals(quadrant, qFrom);
				assertEquals(Location.location(1, 1), lFrom);
				assertEquals(quadrant, qTo);
				assertEquals(Location.location(4, 4), lTo);
			}
		});

		app.random = new Random(new StubRandomNumberFactory(new double[] {0}, new int[] {1,1,2,2,5}));
		presenter.onDockInStarbaseButtonClicked();

		assertEquals(1, events.getFiredCount(ThingMovedEvent.TYPE));
		assertEquals(Location.location(4, 4), enterprise.getLocation());

		assertEquals(1, events.getFiredCount(EnterpriseRepairedEvent.TYPE));
		assertEquals(enterprise.getTorpedos().getMaximum(), enterprise.getTorpedos().getValue(), 0.1);
	}

	@Test
	public void test_updateQuadrantHeader_klingon_near() {
		enterprise.setLocation(Location.location(1, 1));
		Klingon k = new Klingon(ShipClass.Raider);
		k.setLocation(Location.location(3, 3));
		quadrant.getKlingons().add(k);
		presenter.updateQuadrantHeader();
		
		verify(view).setQuadrantName("test", "red-alert");
	}

	@Test
	public void test_updateQuadrantHeader_klingon_far() {
		enterprise.setLocation(Location.location(1, 1));
		Klingon k = new Klingon(ShipClass.Raider);
		k.setLocation(Location.location(5, 7));
		quadrant.getKlingons().add(k);
		presenter.updateQuadrantHeader();
		
		verify(view).setQuadrantName("test", "yellow-alert");
	}
	
	@Test
	public void test_updateQuadrantHeader() {
		enterprise.setLocation(Location.location(1, 1));
		presenter.updateQuadrantHeader();
		
		verify(view).setQuadrantName("test", "");
	}
	
	@Test
	public void test_updateShieldsView() {
		enterprise.getShields().damage(10);
		presenter.updateShieldsView();
		verify(view).updateShields(90, 90, 100);
	}


}
