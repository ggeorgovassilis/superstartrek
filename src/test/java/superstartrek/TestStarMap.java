package superstartrek;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Star;
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
		quadrant.add(new Star(0, 0, StarClass.A));
		quadrant.add(new Star(2, 1, StarClass.A));
		quadrant.add(new Star(1, 2, StarClass.A));
	}
	
	
	@Test
	public void test_findFreeSpotAround_1() {
		Location testLocation = Location.location(1, 1);
		Location free = starMap.findFreeSpotAround(quadrant, testLocation, 1);
		assertEquals(1, free.x);
		assertEquals(1, free.y);
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
		quadrant.add(new Star(1, 1, StarClass.A));
		Location testLocation = Location.location(1, 1);
		Location free = starMap.findFreeSpotAround(quadrant, testLocation, 1);
		assertEquals(2, free.x);
		assertEquals(2, free.y);
	}
}
