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

public class TestQuadrant extends BaseTest{

	@Test
	public void test_findThingAt_1() {
		Klingon k = new Klingon(ShipClass.BirdOfPrey);
		k.setLocation(Location.location(1, 1));
		quadrant.getKlingons().add(k);

		k = new Klingon(ShipClass.BirdOfPrey);
		k.setLocation(Location.location(2, 3));
		quadrant.getKlingons().add(k);

		Star star = new Star(4, 4, StarClass.A);
		quadrant.getStars().add(star);

		StarBase starBase = new StarBase(Location.location(5, 6));
		quadrant.setStarBase(starBase);

		assertTrue(quadrant.findThingAt(2, 3) instanceof Klingon);
		assertTrue(quadrant.findThingAt(1, 1) instanceof Klingon);
		assertEquals(enterprise, quadrant.findThingAt(0, 0));

	}

	@Test
	public void test_findThingAt_2() {
		Klingon k = new Klingon(ShipClass.BirdOfPrey);
		k.setLocation(Location.location(1, 1));
		quadrant.getKlingons().add(k);

		k = new Klingon(ShipClass.BirdOfPrey);
		k.setLocation(Location.location(2, 3));
		quadrant.getKlingons().add(k);

		Star star = new Star(4, 4, StarClass.A);
		quadrant.getStars().add(star);

		StarBase starBase = new StarBase(Location.location(5, 6));
		quadrant.setStarBase(starBase);

		Quadrant q2 = new Quadrant("test2", 7, 7);
		starMap.setQuadrant(q2);
		enterprise.setQuadrant(q2);

		assertTrue(quadrant.findThingAt(2, 3) instanceof Klingon);
		assertTrue(quadrant.findThingAt(1, 1) instanceof Klingon);
		assertNull(quadrant.findThingAt(0, 0));

	}

	@Test
	public void test_findThingAt_3() {
		enterprise.setQuadrant(new Quadrant("asdasd",2,2));
		starMap.enterprise = enterprise;

		for (int x = 0; x < 8; x++)
			for (int y = 0; y < 8; y++)
				assertNull(quadrant.findThingAt(x, y));

	}
}
