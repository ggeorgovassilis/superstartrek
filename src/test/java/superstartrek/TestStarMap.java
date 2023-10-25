package superstartrek;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import superstartrek.client.space.Location;
import superstartrek.client.space.Quadrant;
import superstartrek.client.space.Star;
import superstartrek.client.space.Star.StarClass;
import superstartrek.client.utils.BrowserAPI;

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
		quadrant.add(new Star(Location.location(0, 0), StarClass.A));
		quadrant.add(new Star(Location.location(2, 1), StarClass.A));
		quadrant.add(new Star(Location.location(1, 2), StarClass.A));
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
		BrowserAPI browserAPI = application.browserAPI;
		when(browserAPI.nextInt(2)).thenReturn(1);
		when(browserAPI.nextInt(2)).thenReturn(0);
		when(browserAPI.nextInt(2)).thenReturn(0);
		when(browserAPI.nextInt(2)).thenReturn(1);
		when(browserAPI.nextInt(1)).thenReturn(1);
		when(browserAPI.nextInt(1)).thenReturn(1);
		when(browserAPI.nextInt(1)).thenReturn(0);
		quadrant.add(new Star(Location.location(1, 1), StarClass.A));
		Location testLocation = Location.location(1, 1);
		Location free = starMap.findFreeSpotAround(quadrant, testLocation, 1);
		assertEquals(2, free.x);
		assertEquals(2, free.y);
	}
}
