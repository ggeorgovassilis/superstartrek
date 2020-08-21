package superstartrek;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.QuadrantIndex;
import superstartrek.client.model.Star;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Star.StarClass;
import static org.mockito.Mockito.*;
public class TestStarMap extends BaseTest{

	@Before
	public void setup() {
		quadrant = new Quadrant("q11", 1, 1);
		starMap.setQuadrant(quadrant);
		// *..  
		// .X*
		// .*.
		// 
		// . = empty space, X = tested location, * = star
		quadrant.getStars().add(new Star(0, 0, StarClass.A));
		quadrant.getStars().add(new Star(2, 1, StarClass.A));
		quadrant.getStars().add(new Star(1, 2, StarClass.A));
	}
	
	
	@Test
	public void test_findFreeSpotAround_1() {
		Location testLocation = Location.location(1, 1);
		Location free = starMap.findFreeSpotAround(new QuadrantIndex(quadrant, starMap), testLocation, 1);
		assertEquals(1, free.getX());
		assertEquals(1, free.getY());
	}

	@Test
	public void test_findFreeSpotAround_2() {
		when(application.browserAPI.nextInt(2)).thenReturn(1);
		when(application.browserAPI.nextInt(2)).thenReturn(0);
		when(application.browserAPI.nextInt(2)).thenReturn(0);
		when(application.browserAPI.nextInt(2)).thenReturn(1);
		when(application.browserAPI.nextInt(1)).thenReturn(1);
		when(application.browserAPI.nextInt(1)).thenReturn(1);
		when(application.browserAPI.nextInt(1)).thenReturn(0);
		quadrant.getStars().add(new Star(1, 1, StarClass.A));
		Location testLocation = Location.location(1, 1);
		Location free = starMap.findFreeSpotAround(new QuadrantIndex(quadrant, starMap), testLocation, 1);
		assertEquals(2, free.getX());
		assertEquals(2, free.getY());
	}
}
