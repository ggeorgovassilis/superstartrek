package superstartrek;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import superstartrek.client.activities.computer.ComputerPresenter;
import superstartrek.client.activities.computer.IComputerScreen;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.Klingon.ShipClass;
import superstartrek.client.activities.navigation.ThingMovedHandler;
import superstartrek.client.activities.navigation.ThingMovedHandler.ThingMovedEvent;
import superstartrek.client.control.ScoreKeeper;
import superstartrek.client.control.TurnStartedEvent;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarBase;
import superstartrek.client.model.Thing;
import superstartrek.client.activities.navigation.EnterpriseRepairedHandler.EnterpriseRepairedEvent;;

public class TestComputerPresenter extends BaseTest{

	ComputerPresenter presenter;
	IComputerScreen view;
	ScoreKeeper scoreKeeper;

	@Before
	public void setup() {
		scoreKeeper = mock(ScoreKeeper.class);
		presenter = new ComputerPresenter(application, scoreKeeper);
		view = mock(IComputerScreen.class);
		presenter.setView(view);
	}
	
	@Test
	public void testOnTurnStarted_1() {
		TurnStartedEvent evt = new TurnStartedEvent();
		presenter.onTurnStarted(evt);

		verify(view).setRepairButtonEnabled(false);
		verify(view).setRepairButtonCss("has-repair");
		verify(view).showStarDate("2100");
		verify(view).setQuadrantName("test quadrant 1:2", "");
		verify(view).updateAntimatter(1000,1000);

	}

	@Test
	public void testOnTurnStarted_2() {
		enterprise.setLocation(Location.location(1, 1));
		enterprise.getPhasers().damage(10);
		quadrant.setStarBase(new StarBase(Location.location(3, 3)));
		TurnStartedEvent evt = new TurnStartedEvent();
		presenter.onTurnStarted(evt);

		verify(view).setRepairButtonEnabled(true);
		verify(view).setRepairButtonCss("has-dock");
		verify(view).showStarDate("2100");
		verify(view).setQuadrantName("test quadrant 1:2", "");
		verify(view).updateAntimatter(1000,1000);
		verify(view).updateShields(100, 100, 100);
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
		when(browser.nextDouble()).thenReturn(0.0);
		when(browser.nextInt(any(int.class))).thenReturn(1,1,2,2,5);
		presenter.dockInStarbase();

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
		
		verify(view).setQuadrantName("test quadrant 1:2", "red-alert");
	}

	@Test
	public void test_updateQuadrantHeader_klingon_far() {
		enterprise.setLocation(Location.location(1, 1));
		Klingon k = new Klingon(ShipClass.Raider);
		k.setLocation(Location.location(5, 7));
		quadrant.getKlingons().add(k);
		presenter.updateQuadrantHeader();
		
		verify(view).setQuadrantName("test quadrant 1:2", "yellow-alert");
	}
	
	@Test
	public void test_updateQuadrantHeader() {
		enterprise.setLocation(Location.location(1, 1));
		presenter.updateQuadrantHeader();
		
		verify(view).setQuadrantName("test quadrant 1:2", "");
	}
	
	@Test
	public void test_updateShieldsView() {
		enterprise.getShields().damage(10);
		presenter.updateShieldsView();
		verify(view).updateShields(90, 90, 100);
	}


}
