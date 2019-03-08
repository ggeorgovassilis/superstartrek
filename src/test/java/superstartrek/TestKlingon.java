package superstartrek;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.shared.testing.CountingEventBus;

import superstartrek.client.Application;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.Klingon.ShipClass;
import superstartrek.client.activities.navigation.ThingMovedEvent;
import superstartrek.client.activities.navigation.ThingMovedHandler;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Setup;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;


public class TestKlingon {

	Application app;
	Klingon klingon;
	CountingEventBus events;
	StarMap map;
	Quadrant quadrant;
	Enterprise enterprise;

	@Before
	public void setup() {
		app  = new Application();
		app.events = events = new CountingEventBus();
		
		quadrant = new Quadrant("test", 1,2);
		map = new StarMap();
		map.setQuadrant(quadrant);
		app.starMap = map;
		
		enterprise = new Enterprise(app);
		enterprise.setQuadrant(quadrant);
		map.enterprise = enterprise;
		klingon = new Klingon(app, ShipClass.Raider);
		klingon.setQuadrant(quadrant);
	}

	@Test
	public void testReppositionKlingon() {
		klingon.jumpTo(new Location(1,3));
		enterprise.setLocation(new Location(2,7));
		AtomicReference<ThingMovedEvent> evt = new AtomicReference<ThingMovedEvent>();
		events.addHandler(ThingMovedEvent.TYPE, new ThingMovedHandler() {
			
			@Override
			public void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
				evt.set(new ThingMovedEvent(thing, qFrom, lFrom, qTo, lTo));
			}
		});
		klingon.repositionKlingon();
		assertEquals(new Location(1,4), klingon.getLocation());
		assertEquals(1, events.getHandlerCount(ThingMovedEvent.TYPE));
		assertEquals(quadrant, evt.get().qFrom);
		assertEquals(quadrant, evt.get().qTo);
		assertEquals(klingon, evt.get().thing);
		assertEquals(new Location(1,3), evt.get().lFrom);
		assertEquals(new Location(1,4), evt.get().lTo);
	}
}
