package superstartrek;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import superstartrek.client.Application;
import superstartrek.client.activities.navigation.NavigationHandler;
import superstartrek.client.eventbus.Events;
import superstartrek.client.space.Location;
import superstartrek.client.space.Quadrant;
import superstartrek.client.space.Star;
import superstartrek.client.space.Thing;
import superstartrek.client.space.Star.StarClass;
import superstartrek.client.vessels.CombatHandler;
import superstartrek.client.vessels.Klingon;
import superstartrek.client.vessels.Vessel;
import superstartrek.client.vessels.Weapon;
import superstartrek.client.vessels.Klingon.ShipClass;

public class TestKlingon extends BaseTest{

	Klingon klingon;

	@Before
	public void setup() {
		klingon = new Klingon(ShipClass.Raider);
		quadrant.add(klingon);
	}

	@After
	public void after() {
		Application.set(null);
	}

	@Test
	public void testReppositionKlingon() {
		klingon.jumpTo(Location.location(1, 3));
		enterprise.setLocation(Location.location(2, 7));
		bus.addHandler(Events.THING_MOVED, new NavigationHandler() {

			@Override
			public void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
				assertEquals(quadrant, qFrom);
				assertEquals(quadrant, qTo);
				assertEquals(klingon, thing);
				assertEquals(Location.location(1, 3), lFrom);
				assertEquals(Location.location(0, 4), lTo);
			}
		});
		klingon.repositionKlingon(quadrant);
		assertEquals(2, bus.getFiredCount(Events.THING_MOVED));

		// a*+ moves a bit strangely; it can move temporarily away from a target (even
		// if that is not necessary)
		// as long as the path is optimal
	}

	@Test
	public void testFireOnEnterprise() {
		when(browser.nextDouble()).thenReturn(0.5, 0.6, 0.1, 0.3, 0.3, 0.4);
		when(browser.nextInt(any(int.class))).thenReturn(1,2,3,4);
		klingon.jumpTo(Location.location(1, 3));
		enterprise.setLocation(Location.location(2, 3));
		bus.addHandler(Events.AFTER_FIRE, new CombatHandler() {

			@Override
			public void afterFire(Quadrant quadrant, Vessel actor, Thing target, Weapon weapon, double damage, boolean wasAutoFire) {
				assertEquals(klingon, actor);
				assertEquals(enterprise, target);
				assertEquals(Weapon.disruptor, weapon);
				assertEquals(10, damage, 0.1);
			}
		});

		klingon.fireOnEnterprise(quadrant);
		assertEquals(1, bus.getFiredCount(Events.AFTER_FIRE));
	}

	@Test
	public void test_flee() {
		when(browser.nextInt(any(int.class))).thenReturn(1,1);
		klingon.setLocation(Location.location(3, 2));
		klingon.uncloak();
		bus.addHandler(Events.THING_MOVED, new NavigationHandler() {

			@Override
			public void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
				assertEquals(klingon, thing);
				assertEquals(quadrant, qFrom);
				assertEquals(Location.location(3, 2), lFrom);
				assertEquals(Location.location(4, 3), lTo);
				assertEquals(quadrant, qTo);
			}

		});
		klingon.flee(quadrant);
		assertEquals(1, bus.getFiredCount(Events.THING_MOVED));
	}
	
	@Test
	public void test_hasClearShotAt() {
		klingon.setLocation(Location.location(3, 3));
		enterprise.setLocation(Location.location(3, 5));
		assertTrue(klingon.hasClearShotAt(enterprise.getQuadrant(), enterprise.getLocation(), enterprise, starMap));
	}
	
	@Test
	public void test_hasClearShotAt_obstructed() {
		klingon.setLocation(Location.location(3, 3));
		enterprise.setLocation(Location.location(3, 5));
		Star star = new Star(Location.location(3, 4), StarClass.A);
		quadrant.add(star);
		assertFalse(klingon.hasClearShotAt(enterprise.getQuadrant(), enterprise.getLocation(), enterprise, starMap));
	}
	
	@Test
	public void test_cloaking_after_quadrant_changed() {
		when(browser.nextInt(any(int.class))).thenReturn(7,7,7,7);
		klingon.uncloak();
		klingon.onActiveQuadrantChanged(quadrant, quadrant);
		assertFalse(klingon.isVisible());
	}
	
	@Test
	public void test_destroy() {
		assertEquals(klingon, quadrant.findThingAt(klingon.getLocation()));
		klingon.destroy();
		assertNull(quadrant.findThingAt(klingon.getLocation()));
	}
}
