package superstartrek;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.atomic.AtomicReference;

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
import superstartrek.client.activities.navigation.ThingMovedEvent;
import superstartrek.client.activities.navigation.ThingMovedHandler;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;
import superstartrek.client.utils.Random;
import superstartrek.client.utils.RandomNumberFactory;


public class TestKlingon {

	Application app;
	Klingon klingon;
	CountingEventBus events;
	StarMap map;
	Quadrant quadrant;
	Enterprise enterprise;

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
		klingon = new Klingon(ShipClass.Raider);
		klingon.setQuadrant(quadrant);
	}

	@Test
	public void testReppositionKlingon() {
		klingon.jumpTo(Location.location(1,3));
		enterprise.setLocation(Location.location(2,7));
		AtomicReference<ThingMovedEvent> evt = new AtomicReference<ThingMovedEvent>();
		events.addHandler(ThingMovedEvent.TYPE, new ThingMovedHandler() {
			
			@Override
			public void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
				evt.set(new ThingMovedEvent(thing, qFrom, lFrom, qTo, lTo));
			}
		});
		klingon.repositionKlingon();
			
		//a*+ moves a bit strangely; it can move temporarily away from a target (even if that is not necessary)
		//as long as the path is optimal
		assertEquals(2, events.getFiredCount(ThingMovedEvent.TYPE));
		assertEquals(quadrant, evt.get().qFrom);
		assertEquals(quadrant, evt.get().qTo);
		assertEquals(klingon, evt.get().thing);
		assertEquals(Location.location(1,3), evt.get().lFrom);
		assertEquals(Location.location(0,4), evt.get().lTo);
	}

	@Test
	public void testFireOnEnterprise() {
		RandomNumberFactory random = mock(RandomNumberFactory.class);
		when(random.nextDouble()).thenAnswer(new Answer<Double>() {
			int counter = 0;
			double numbers[]= {0.5,0.6,0.1};
			@Override
			public Double answer(InvocationOnMock invocation) throws Throwable {
				return numbers[counter++];
			}
		});
		Random.setFactory(random);
		klingon.jumpTo(Location.location(1,3));
		enterprise.setLocation(Location.location(2,3));
		events.addHandler(FireEvent.TYPE, new FireHandler() {
			
			@Override
			public void onFire(FireEvent evt) {
				assertEquals(klingon, evt.actor);
				assertEquals(enterprise, evt.target);
				assertEquals("disruptor", evt.weapon);
				assertEquals(10,evt.damage,0.1);
			}
			
			@Override
			public void afterFire(FireEvent evt) {
				assertEquals(klingon, evt.actor);
				assertEquals(enterprise, evt.target);
				assertEquals("disruptor", evt.weapon);
				assertEquals(10,evt.damage,0.1);
			}
		});
		
		
		klingon.fireOnEnterprise();
		assertEquals(2, events.getFiredCount(FireEvent.TYPE));
	}
}
