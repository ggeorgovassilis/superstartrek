package superstartrek;

import org.junit.Before;
import org.junit.Test;

import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.model.Constants;
import superstartrek.client.model.Location;
import superstartrek.client.model.Setup;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
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
		final int NUMBER_OF_KLINGONS = 2;
		when(browser.nextDouble()).thenReturn(0.1);
		when(browser.nextInt(any(int.class))).thenReturn(
				//@formatter:off
				// stars
										NUMBER_OF_STARS-Constants.MIN_STARS_IN_QUADRANT,
										2,3,1,
										1,1,2,
										3,1,0,
										0,1,2,
										7,0,3,
				// klingons
										NUMBER_OF_KLINGONS,
										0,1,5,
										1,2,2,
										0,3,2
);
		//@formatter:on

		quadrant = setup.makeQuadrant(starMap, 1, 2);
		assertEquals(5, quadrant.getStars().size());

		assertEquals(Location.location(2, 3), quadrant.getStars().get(0).getLocation());
		assertEquals("Class B star", quadrant.getStars().get(0).getName());
		assertEquals("star star-class-b", quadrant.getStars().get(0).getCss());

		assertEquals(Location.location(1, 1), quadrant.getStars().get(1).getLocation());
		assertEquals("Class A star", quadrant.getStars().get(1).getName());
		assertEquals("star star-class-a", quadrant.getStars().get(1).getCss());

		assertEquals(Location.location(3, 1), quadrant.getStars().get(2).getLocation());
		assertEquals("Class O star", quadrant.getStars().get(2).getName());
		assertEquals("star star-class-o", quadrant.getStars().get(2).getCss());

		assertEquals(Location.location(0, 1),quadrant.getStars().get(3).getLocation());
		assertEquals("Class A star",quadrant.getStars().get(3).getName());
		assertEquals("star star-class-a", quadrant.getStars().get(3).getCss());

		assertEquals(Location.location(7, 0),quadrant.getStars().get(4).getLocation());
		assertEquals("Class F star",quadrant.getStars().get(4).getName());
		assertEquals("star star-class-f", quadrant.getStars().get(4).getCss());
		
		assertEquals(NUMBER_OF_KLINGONS+1,quadrant.getKlingons().size());
		
		List<Klingon> klingons = new ArrayList<>(quadrant.getKlingons());
		assertEquals(Location.location(1, 5), klingons.get(0).getLocation());
		assertEquals("a Klingon raider",klingons.get(0).getName());

		assertEquals(Location.location(2, 2), klingons.get(1).getLocation());
		assertEquals("a Bird-of-prey",klingons.get(1).getName());

		assertEquals(Location.location(3, 2), klingons.get(2).getLocation());
		assertEquals("a Klingon raider",klingons.get(2).getName());
	}
}
