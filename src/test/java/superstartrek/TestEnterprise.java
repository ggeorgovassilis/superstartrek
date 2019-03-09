package superstartrek;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.shared.testing.CountingEventBus;

import superstartrek.client.Application;
import superstartrek.client.activities.messages.MessageEvent;
import superstartrek.client.activities.messages.MessageHandler;
import superstartrek.client.activities.navigation.EnterpriseWarpedEvent;
import superstartrek.client.activities.navigation.EnterpriseWarpedHandler;
import superstartrek.client.activities.navigation.ThingMovedEvent;
import superstartrek.client.activities.navigation.ThingMovedHandler;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;

import static org.mockito.Mockito.*;

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
		map.enterprise = enterprise = new Enterprise(application);
		
	}
	
	@Test
	public void testDamageTorpedos() {
		MessageHandler handler = mock(MessageHandler.class);
		events.addHandler(MessageEvent.TYPE, handler);

		enterprise.damageTorpedos();
		
		assertFalse(enterprise.getTorpedos().isEnabled());
		verify(handler).showMessage(eq("Torpedo bay damaged"), eq("enterprise-damaged"));
	}
	
	@Test
	public void testDamagePhasers() {
		MessageHandler handler = mock(MessageHandler.class);
		events.addHandler(MessageEvent.TYPE, handler);

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
		verify(handler, times(4)).showMessage(eq("Phaser array damaged"), eq("enterprise-damaged"));
	}

	@Test
	public void testDamageImpulse() {
		MessageHandler handler = mock(MessageHandler.class);
		events.addHandler(MessageEvent.TYPE, handler);

		enterprise.damageImpulse();
		assertTrue(enterprise.getImpulse().isEnabled());
		assertEquals(2, enterprise.getImpulse().getCurrentUpperBound(), 0.1);
		
		enterprise.damageImpulse();
		assertTrue(enterprise.getImpulse().isEnabled());
		assertEquals(1, enterprise.getImpulse().getCurrentUpperBound(), 0.1);

		enterprise.damageImpulse();
		assertFalse(enterprise.getImpulse().isEnabled());
		assertEquals(0, enterprise.getImpulse().getCurrentUpperBound(), 0.1);

		verify(handler, times(3)).showMessage(eq("Impulse drive damaged"), eq("enterprise-damaged"));
		
	}
	
	@Test
	public void testNavigateTo() {
		Quadrant quadrant = new Quadrant("q 1 2", 1, 2);
		map.setQuadrant(quadrant);
		enterprise.setQuadrant(quadrant);
		enterprise.setLocation(new Location(0,0));
		events.addHandler(ThingMovedEvent.TYPE, new ThingMovedHandler() {
			
			@Override
			public void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
				assertEquals(enterprise, thing);
				assertEquals(quadrant, qFrom);
				assertEquals(quadrant, qTo);
				assertEquals(new Location(2,2), lTo);
			}
		});
		
		
		enterprise.navigateTo(new Location(2,2));
		
		assertEquals(0, enterprise.getImpulse().getValue(), 0.1);
		assertEquals(new Location(2,2), enterprise.getLocation());
		assertEquals(1, events.getFiredCount(ThingMovedEvent.TYPE));
	}
	
	@Test
	public void testWarpTo() {
		Quadrant quadrant = new Quadrant("q 1 2", 1, 2);
		map.setQuadrant(quadrant);
		enterprise.setQuadrant(quadrant);
		enterprise.setLocation(new Location(0,0));
		
		//path between source and target needs to exist for collision check
		map.setQuadrant(new Quadrant("_", 2,3));
		
		//quadrants around target need to exist because exploration flag is set
		for (int x = 3-1;x<=3+1;x++)
			for (int y = 4-1;y<=4+1;y++)
				map.setQuadrant(new Quadrant("_", x,y));

		Quadrant targetQuadrant = map.getQuadrant(3, 4);
		map.setQuadrant(targetQuadrant);
		
		events.addHandler(EnterpriseWarpedEvent.TYPE, new EnterpriseWarpedHandler() {
			
			@Override
			public void onEnterpriseWarped(Enterprise e, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
				assertEquals(enterprise, enterprise);
				assertEquals(quadrant, qFrom);
				assertEquals(targetQuadrant, qTo);
				assertEquals(new Location(0,0), lFrom);
				assertEquals(new Location(0,1), lTo);
			}
		});
		enterprise.warpTo(targetQuadrant);
		
		assertEquals(1, events.getFiredCount(EnterpriseWarpedEvent.TYPE));
	}
}
