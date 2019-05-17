package superstartrek;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import superstartrek.client.Application;
import superstartrek.client.activities.combat.FireHandler;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.Klingon.ShipClass;
import superstartrek.client.activities.navigation.ThingMovedHandler;
import superstartrek.client.bus.Events;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.QuadrantIndex;
import superstartrek.client.model.Star;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;
import superstartrek.client.model.Star.StarClass;

public class TestKlingon extends BaseTest{

	Klingon klingon;

	@Before
	public void setup() {
		klingon = new Klingon(ShipClass.Raider);
		quadrant.getKlingons().add(klingon);
	}

	@After
	public void after() {
		Application.set(null);
	}

	@Test
	public void testReppositionKlingon() {
		klingon.jumpTo(Location.location(1, 3));
		enterprise.setLocation(Location.location(2, 7));
		bus.register(Events.THING_MOVED, new ThingMovedHandler() {

			@Override
			public void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
				assertEquals(quadrant, qFrom);
				assertEquals(quadrant, qTo);
				assertEquals(klingon, thing);
				assertEquals(Location.location(1, 3), lFrom);
				assertEquals(Location.location(0, 4), lTo);
			}
		});
		klingon.repositionKlingon(new QuadrantIndex(quadrant, starMap));
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
		bus.register(Events.AFTER_FIRE, new FireHandler() {

			@Override
			public void afterFire(Quadrant quadrant, Vessel actor, Thing target, String weapon, double damage, boolean wasAutoFire) {
				assertEquals(klingon, actor);
				assertEquals(enterprise, target);
				assertEquals("disruptor", weapon);
				assertEquals(10, damage, 0.1);
			}
		});

		klingon.fireOnEnterprise(new QuadrantIndex(quadrant, starMap));
		assertEquals(1, bus.getFiredCount(Events.AFTER_FIRE));
	}

	@Test
	public void test_flee() {
		when(browser.nextInt(any(int.class))).thenReturn(1,1);
		klingon.setLocation(Location.location(3, 2));
		bus.register(Events.THING_MOVED, new ThingMovedHandler() {

			@Override
			public void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
				assertEquals(klingon, thing);
				assertEquals(quadrant, qFrom);
				assertEquals(Location.location(3, 2), lFrom);
				assertEquals(Location.location(4, 3), lTo);
				assertEquals(quadrant, qTo);
			}

		});
		klingon.flee(new QuadrantIndex(quadrant, starMap));
		assertEquals(1, bus.getFiredCount(Events.THING_MOVED));
	}
	
	@Test
	public void test_hasClearShotAt() {
		klingon.setLocation(Location.location(3, 3));
		enterprise.setLocation(Location.location(3, 5));
		QuadrantIndex index = new QuadrantIndex(enterprise.getQuadrant(), starMap);
		assertTrue(klingon.hasClearShotAt(index, enterprise.getLocation(), enterprise, starMap));
	}
	
	@Test
	public void test_hasClearShotAt_obstructed() {
		klingon.setLocation(Location.location(3, 3));
		enterprise.setLocation(Location.location(3, 5));
		Star star = new Star(3, 4, StarClass.A);
		quadrant.getStars().add(star);
		QuadrantIndex index = new QuadrantIndex(enterprise.getQuadrant(), starMap);
		assertFalse(klingon.hasClearShotAt(index, enterprise.getLocation(), enterprise, starMap));
	}
}
