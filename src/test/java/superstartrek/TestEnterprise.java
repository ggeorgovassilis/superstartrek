package superstartrek;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.gwt.event.shared.testing.CountingEventBus;

import superstartrek.client.Application;
import superstartrek.client.activities.combat.FireHandler.*;
import superstartrek.client.activities.combat.FireHandler;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.Klingon.ShipClass;
import superstartrek.client.activities.messages.MessageHandler;
import superstartrek.client.activities.navigation.EnterpriseWarpedEvent;
import superstartrek.client.activities.navigation.EnterpriseWarpedHandler;
import superstartrek.client.activities.navigation.ThingMovedEvent;
import superstartrek.client.activities.navigation.ThingMovedHandler;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Star;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Star.StarClass;
import superstartrek.client.utils.Random;
import superstartrek.client.utils.RandomNumberFactory;

import static org.mockito.Mockito.*;

import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class TestEnterprise {
	Enterprise enterprise;
	Application application;
	CountingEventBus events;
	StarMap map;

	@Before
	public void setup() {
		application = new Application();
		application.events = events = new CountingEventBus();
		application.starMap = map = new StarMap();
		map.enterprise = enterprise = new Enterprise(application, map);

	}

	@Test
	public void testDamageTorpedos() {
		MessageHandler handler = mock(MessageHandler.class);
		events.addHandler(MessageHandler.MessagePostedEvent.TYPE, handler);

		enterprise.damageTorpedos();

		assertFalse(enterprise.getTorpedos().isEnabled());
		verify(handler).messagePosted(eq("Torpedo bay damaged"), eq("enterprise-damaged"));
	}

	@Test
	public void testDamagePhasers() {
		MessageHandler handler = mock(MessageHandler.class);
		events.addHandler(MessageHandler.MessagePostedEvent.TYPE, handler);

		enterprise.damagePhasers();
		assertTrue(enterprise.getPhasers().isEnabled());
		assertEquals(105, enterprise.getPhasers().getCurrentUpperBound(), 0.1);
		enterprise.damagePhasers();
		assertTrue(enterprise.getPhasers().isEnabled());
		assertEquals(60, enterprise.getPhasers().getCurrentUpperBound(), 0.1);
		enterprise.damagePhasers();
		assertTrue(enterprise.getPhasers().isEnabled());
		assertEquals(15, enterprise.getPhasers().getCurrentUpperBound(), 0.1);
		enterprise.damagePhasers();
		assertEquals(0, enterprise.getPhasers().getCurrentUpperBound(), 0.1);
		assertFalse(enterprise.getPhasers().isEnabled());
		verify(handler, times(4)).messagePosted(eq("Phaser array damaged"), eq("enterprise-damaged"));
	}

	@Test
	public void testDamageImpulse() {
		MessageHandler handler = mock(MessageHandler.class);
		events.addHandler(MessageHandler.MessagePostedEvent.TYPE, handler);

		enterprise.damageImpulse();
		assertTrue(enterprise.getImpulse().isEnabled());
		assertEquals(2, enterprise.getImpulse().getCurrentUpperBound(), 0.1);

		enterprise.damageImpulse();
		assertTrue(enterprise.getImpulse().isEnabled());
		assertEquals(1, enterprise.getImpulse().getCurrentUpperBound(), 0.1);

		enterprise.damageImpulse();
		assertFalse(enterprise.getImpulse().isEnabled());
		assertEquals(0, enterprise.getImpulse().getCurrentUpperBound(), 0.1);

		verify(handler, times(3)).messagePosted(eq("Impulse drive damaged"), eq("enterprise-damaged"));

	}

	@Test
	public void testNavigateTo() {
		Quadrant quadrant = new Quadrant("q 1 2", 1, 2);
		map.setQuadrant(quadrant);
		enterprise.setQuadrant(quadrant);
		enterprise.setLocation(Location.location(0, 0));
		events.addHandler(ThingMovedEvent.TYPE, new ThingMovedHandler() {

			@Override
			public void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
				assertEquals(enterprise, thing);
				assertEquals(quadrant, qFrom);
				assertEquals(quadrant, qTo);
				assertEquals(Location.location(2, 2), lTo);
			}
		});

		enterprise.navigateTo(Location.location(2, 2));

		assertEquals(0, enterprise.getImpulse().getValue(), 0.5);
		assertEquals(Location.location(2, 2), enterprise.getLocation());
		assertEquals(1, events.getFiredCount(ThingMovedEvent.TYPE));
	}

	@Test
	public void testWarpTo() {
		Quadrant quadrant = new Quadrant("q 1 2", 1, 2);
		map.setQuadrant(quadrant);
		enterprise.setQuadrant(quadrant);
		enterprise.setLocation(Location.location(0, 0));

		// path between source and target needs to exist for collision check
		map.setQuadrant(new Quadrant("_", 2, 3));

		// quadrants around target need to exist because exploration flag is set
		for (int x = 3 - 1; x <= 3 + 1; x++)
			for (int y = 4 - 1; y <= 4 + 1; y++)
				map.setQuadrant(new Quadrant("_", x, y));

		Quadrant targetQuadrant = map.getQuadrant(3, 4);
		map.setQuadrant(targetQuadrant);

		events.addHandler(EnterpriseWarpedEvent.TYPE, new EnterpriseWarpedHandler() {

			@Override
			public void onEnterpriseWarped(Enterprise e, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
				assertEquals(enterprise, enterprise);
				assertEquals(quadrant, qFrom);
				assertEquals(targetQuadrant, qTo);
				assertEquals(Location.location(0, 0), lFrom);
				assertEquals(Location.location(0, 1), lTo);
			}
		});
		enterprise.warpTo(targetQuadrant, null);

		assertEquals(1, events.getFiredCount(EnterpriseWarpedEvent.TYPE));
	}

	@Test
	public void testFirePhasers() {
		RandomNumberFactory random = mock(RandomNumberFactory.class);
		when(random.nextDouble()).thenAnswer(new Answer<Double>() {
			int counter = 0;
			double numbers[]= {0.5,0.6,0.1,0.3,0.3};
			@Override
			public Double answer(InvocationOnMock invocation) throws Throwable {
				return numbers[counter++];
			}
		});
		application.random = new Random(random);
		Quadrant quadrant = new Quadrant("q 1 2", 1, 2);
		map.setQuadrant(quadrant);
		enterprise.setQuadrant(quadrant);
		
		Klingon klingon = new Klingon(ShipClass.BirdOfPrey);
		quadrant.getKlingons().add(klingon);
		klingon.setLocation(Location.location(1, 1));
		klingon.registerActionHandlers();
		klingon.uncloak();
		
		FireHandler handler = mock(FireHandler.class);
		
		Application.get().events.addHandler(FireEvent.TYPE, handler);

		assertEquals(100, klingon.getShields().getValue(), 0.1 );
		enterprise.firePhasersAt(klingon.getLocation(), false);
		assertEquals(78, klingon.getShields().getValue(), 10 );
		
		//once for before phase + once after phase
		assertEquals(2, events.getFiredCount(FireEvent.TYPE));
		verify(handler, times(1)).afterFire(argThat(new BaseMatcher<FireEvent>() {

			@Override
			public boolean matches(Object o) {
				FireEvent evt = (FireEvent)o;
				return evt.wasAutoFire == false && evt.actor == enterprise && evt.target == klingon && Math.abs(evt.damage -20)<2;
			}

			@Override
			public void describeTo(Description desc) {
				desc.appendText("FireEvent");
			}
	    }));
	}

	@Test
	public void testFireTorpedos() {
		
		RandomNumberFactory random = mock(RandomNumberFactory.class);
		when(random.nextDouble()).thenAnswer(new Answer<Double>() {
			int counter = 0;
			double numbers[]= {0.5,0.6,0.1,0.3,0.3};
			@Override
			public Double answer(InvocationOnMock invocation) throws Throwable {
				return numbers[counter++];
			}
		});
		application.random = new Random(random);
		
		Quadrant quadrant = new Quadrant("q 1 2", 1, 2);
		map.setQuadrant(quadrant);
		enterprise.setQuadrant(quadrant);

		Klingon klingon = new Klingon(ShipClass.BirdOfPrey);
		quadrant.getKlingons().add(klingon);
		klingon.setLocation(Location.location(1, 1));
		klingon.registerActionHandlers();
		klingon.uncloak();

		FireHandler handler = mock(FireHandler.class);

		Application.get().events.addHandler(FireEvent.TYPE, handler);

		assertEquals(100, klingon.getShields().getValue(), 0.1);
		enterprise.fireTorpedosAt(klingon.getLocation());
		assertEquals(50, klingon.getShields().getValue(), 10);

		// once for before phase + once after phase
		assertEquals(2, events.getFiredCount(FireEvent.TYPE));
		
		verify(handler, times(1)).onFire(argThat(new BaseMatcher<FireEvent>() {

			@Override
			public boolean matches(Object o) {
				FireEvent evt = (FireEvent)o;
				return evt.wasAutoFire == false && evt.actor == enterprise && evt.target == klingon && evt.weapon.equals("torpedos" )&& Math.abs(evt.damage -50)<2;
			}

			@Override
			public void describeTo(Description desc) {
				desc.appendText("FireEvent");
			}
	    }));
		verify(handler, times(1)).afterFire(argThat(new BaseMatcher<FireEvent>() {

			@Override
			public boolean matches(Object o) {
				FireEvent evt = (FireEvent)o;
				return evt.wasAutoFire == false && evt.actor == enterprise && evt.target == klingon && evt.weapon.equals("torpedos" )&& Math.abs(evt.damage -50)<2;
			}

			@Override
			public void describeTo(Description desc) {
				desc.appendText("FireEvent");
			}
	    }));

	}
	
	@Test
	public void test_getReachableSectors() {
		Quadrant quadrant = new Quadrant("q 1 2", 1, 2);
		map.setQuadrant(quadrant);
		enterprise.setQuadrant(quadrant);
		enterprise.setLocation(Location.location(4, 4));
		quadrant.getStars().add(new Star(1,6,StarClass.A));
		quadrant.getStars().add(new Star(2,6,StarClass.A));
		quadrant.getStars().add(new Star(3,6,StarClass.A));
		quadrant.getStars().add(new Star(5,6,StarClass.A));
		quadrant.getStars().add(new Star(6,6,StarClass.A));
		quadrant.getStars().add(new Star(7,6,StarClass.A));
		quadrant.getStars().add(new Star(4,3,StarClass.A));
		List<Location> list = enterprise.findReachableSectors();
		assertTrue(list.contains(Location.location(4, 5)));
		assertTrue(list.contains(Location.location(4, 6)));
		assertTrue(list.contains(Location.location(5, 5)));
		assertTrue(list.contains(Location.location(3, 3)));
		assertTrue(list.contains(Location.location(3, 4)));
		assertTrue(list.contains(Location.location(5, 4)));
		assertFalse(list.contains(Location.location(5, 6)));
		assertFalse(list.contains(Location.location(3, 6)));
		assertEquals(19,list.size());
	}

	@Test
	public void test_isDamaged() {
		assertFalse(enterprise.isDamaged());
		enterprise.damagePhasers();
		assertTrue(enterprise.isDamaged());
	}
	
	@Test
	public void test_canFirePhaserAt() {
		Quadrant quadrant = new Quadrant("q 1 2", 1, 2);
		map.setQuadrant(quadrant);
		enterprise.setQuadrant(quadrant);
		enterprise.setLocation(Location.location(4, 4));
		
		Star star = new Star(3,3, Star.StarClass.A);
		quadrant.getStars().add(star);
		
		Klingon klingon = new Klingon(Klingon.ShipClass.BirdOfPrey);
		klingon.setLocation(Location.location(5, 5));
		quadrant.getKlingons().add(klingon);
		assertEquals("There is nothing at 7:7",enterprise.canFirePhaserAt(Location.location(7, 7)));
		assertEquals("Phasers can target only enemy vessels",enterprise.canFirePhaserAt(star.getLocation()));
		
		klingon.getCloak().setValue(true);
		assertEquals("There is nothing at 5:5", enterprise.canFirePhaserAt(klingon.getLocation()));
		
		klingon.uncloak();
		assertNull(null, enterprise.canFirePhaserAt(klingon.getLocation()));
		
	
	}
}
