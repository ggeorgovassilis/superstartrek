package superstartrek;

import superstartrek.client.space.Constants;
import superstartrek.client.space.Location;
import superstartrek.client.space.Quadrant;
import superstartrek.client.space.Star;
import superstartrek.client.space.StarBase;
import superstartrek.client.space.Star.StarClass;
import superstartrek.client.vessels.Klingon;
import superstartrek.client.vessels.Klingon.ShipClass;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestQuadrant extends BaseTest{

	@Test
	public void test_findThingAt_1() {
		Klingon k = new Klingon(ShipClass.BirdOfPrey);
		k.setLocation(Location.location(1, 1));
		quadrant.add(k);

		k = new Klingon(ShipClass.BirdOfPrey);
		k.setLocation(Location.location(2, 3));
		quadrant.add(k);

		Star star = new Star(Location.location(4, 4), StarClass.A);
		quadrant.add(star);

		StarBase starBase = new StarBase(Location.location(5, 6));
		quadrant.setStarBase(starBase);
		
		quadrant.add(enterprise);

		assertTrue(quadrant.findThingAt(2, 3) instanceof Klingon);
		assertTrue(quadrant.findThingAt(1, 1) instanceof Klingon);
		assertEquals(enterprise, quadrant.findThingAt(0, 0));

	}

	@Test
	public void test_findThingAt_2() {
		Klingon k = new Klingon(ShipClass.BirdOfPrey);
		k.setLocation(Location.location(1, 1));
		quadrant.add(k);

		k = new Klingon(ShipClass.BirdOfPrey);
		k.setLocation(Location.location(2, 3));
		quadrant.add(k);

		Star star = new Star(Location.location(4, 4), StarClass.A);
		quadrant.add(star);

		StarBase starBase = new StarBase(Location.location(5, 6));
		quadrant.setStarBase(starBase);

		Quadrant q2 = new Quadrant("test2", 7, 7);
		starMap.setQuadrant(q2);
		enterprise.setQuadrant(q2);
		
		quadrant.remove(enterprise);

		assertTrue(quadrant.findThingAt(2, 3) instanceof Klingon);
		assertTrue(quadrant.findThingAt(1, 1) instanceof Klingon);
		assertNull(quadrant.findThingAt(0, 0));

	}

	@Test
	public void test_findThingAt_3() {
		enterprise.setQuadrant(new Quadrant("asdasd",2,2));
		starMap.enterprise = enterprise;

		for (int x = 0; x < Constants.SECTORS_EDGE; x++)
			for (int y = 0; y < Constants.SECTORS_EDGE; y++)
				assertNull(quadrant.findThingAt(x, y));

	}
}
