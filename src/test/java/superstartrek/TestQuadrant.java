package superstartrek;

import superstartrek.client.Application;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.Klingon.ShipClass;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Star;
import superstartrek.client.model.StarBase;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Star.StarClass;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

public class TestQuadrant {

	Quadrant q;
	Application app;
	EventBus events;
	StarMap map;

	@Before
	public void setup() {
		q = new Quadrant("test quadrat", 1, 2);
		app = new Application();
		events = app.events = new SimpleEventBus();
		map = new StarMap();
	}

	@After
	public void cleanup() {
		Application.set(null);
	}

	@Test
	public void test_findThingAt_1() {
		Klingon k = new Klingon(ShipClass.BirdOfPrey);
		k.setLocation(Location.location(1, 1));
		q.getKlingons().add(k);

		k = new Klingon(ShipClass.BirdOfPrey);
		k.setLocation(Location.location(2, 3));
		q.getKlingons().add(k);

		Star star = new Star(4, 4, StarClass.A);
		q.getStars().add(star);

		StarBase starBase = new StarBase(Location.location(5, 6));
		q.setStarBase(starBase);
		map.setQuadrant(q);
		Enterprise enterprise = new Enterprise(app, map);
		enterprise.setQuadrant(q);
		map.enterprise = enterprise;
		app.starMap = map;

		assertTrue(q.findThingAt(2, 3) instanceof Klingon);
		assertTrue(q.findThingAt(1, 1) instanceof Klingon);
		assertEquals(enterprise, q.findThingAt(0, 0));

	}

	@Test
	public void test_findThingAt_2() {
		Klingon k = new Klingon(ShipClass.BirdOfPrey);
		k.setLocation(Location.location(1, 1));
		q.getKlingons().add(k);

		k = new Klingon(ShipClass.BirdOfPrey);
		k.setLocation(Location.location(2, 3));
		q.getKlingons().add(k);

		Star star = new Star(4, 4, StarClass.A);
		q.getStars().add(star);

		StarBase starBase = new StarBase(Location.location(5, 6));
		q.setStarBase(starBase);
		map.setQuadrant(q);
		Enterprise enterprise = new Enterprise(app, map);

		Quadrant q2 = new Quadrant("test2", 7, 7);
		map.setQuadrant(q2);
		enterprise.setQuadrant(q2);

		map.enterprise = enterprise;
		app.starMap = map;

		assertTrue(q.findThingAt(2, 3) instanceof Klingon);
		assertTrue(q.findThingAt(1, 1) instanceof Klingon);
		assertNull(q.findThingAt(0, 0));

	}

	@Test
	public void test_findThingAt_3() {
		Enterprise enterprise = new Enterprise(app, map);
		enterprise.setQuadrant(new Quadrant("asdasd",2,2));
		map.enterprise = enterprise;
		app.starMap = map;

		for (int x = 0; x < 8; x++)
			for (int y = 0; y < 8; y++)
				assertNull(q.findThingAt(x, y));

	}
}
