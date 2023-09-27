package superstartrek;

import org.junit.Before;
import org.junit.Test;

import superstartrek.client.space.Location;
import superstartrek.client.space.Setup;
import superstartrek.client.vessels.Klingon;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

public class TestSetup extends BaseTest{

	Setup setup;

	@Before
	public void setup() {
		setup = new Setup(application);
	}
	
	@Test
	public void test_makeQuadrant() {
		final int NUMBER_OF_STARS = 5;
		when(browser.nextDouble()).thenReturn(0.1,0.2,0.3,0.4,0.6,0.8,0.9,1.0,0.1,0.2,0.3,0.4,0.6,0.8,0.9,1.0,0.1,0.2,0.3,0.4,0.6,0.8,0.9,1.0,0.1,0.2,0.3,0.4,0.6,0.8,0.9,1.0,0.1,0.2,0.3,0.4,0.6,0.8,0.9,1.0);
		when(browser.nextInt(any(int.class))).thenReturn(
				//@formatter:off
				// stars
										NUMBER_OF_STARS-4 /*Constants.MIN_STARS_IN_QUADRANT*/,
										2,3,1,
										1,1,2,
										3,1,0,
										0,1,2,
										7,0,3,
				// klingons
										10, //formation f5
										1,5, // free location
										2,2// free location
);
		//@formatter:on

		quadrant = setup.makeQuadrant(starMap, 1, 2);
		assertEquals(5, quadrant.getStars().size());

		assertEquals(Location.location(2, 3), quadrant.getStars().get(0).getLocation());
		assertEquals("Class O star", quadrant.getStars().get(0).getName());
		assertEquals("star star-class-o", quadrant.getStars().get(0).getCss());

		assertEquals(Location.location(1, 1), quadrant.getStars().get(1).getLocation());
		assertEquals("Asteroid", quadrant.getStars().get(1).getName());
		assertEquals("star star-class-asteroid", quadrant.getStars().get(1).getCss());

		assertEquals(Location.location(1, 2), quadrant.getStars().get(2).getLocation());
		assertEquals("Class A star", quadrant.getStars().get(2).getName());
		assertEquals("star star-class-a", quadrant.getStars().get(2).getCss());

		assertEquals(Location.location(3, 1),quadrant.getStars().get(3).getLocation());
		assertEquals("Asteroid",quadrant.getStars().get(3).getName());
		assertEquals("star star-class-asteroid", quadrant.getStars().get(3).getCss());

		assertEquals(Location.location(0, 0),quadrant.getStars().get(4).getLocation());
		assertEquals("Class A star",quadrant.getStars().get(4).getName());
		assertEquals("star star-class-a", quadrant.getStars().get(4).getCss());
		
		setup.putKlingonsInQuadrant(quadrant, starMap);
		assertEquals(1,quadrant.getKlingons().size());
		
		List<Klingon> klingons = new ArrayList<>(quadrant.getKlingons());
		assertEquals(Location.location(2, 7), klingons.get(0).getLocation());
		assertEquals("a Klingon raider",klingons.get(0).getName());

	}
}
