package superstartrek;

import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import superstartrek.client.Application;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Setup;
import superstartrek.client.model.StarMap;
import superstartrek.client.utils.Random;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;

public class TestSetup {

	Setup setup;
	Application application;
	EventBus bus;

	@Before
	public void setup() {
		application = new Application();
		bus = new SimpleEventBus();
		application.events = bus;
		setup = new Setup(application);
	}
	
	@After
	public void cleanup() {
		Application.set(null);
	}


	@Test
	public void test_makeQuadrant() {
		StarMap map = new StarMap();
		map.enterprise = new Enterprise(application, map);
		final int NUMBER_OF_STARS = 5;
		final int NUMBER_OF_KLINGONS = 2;
		application.random = new Random(new StubRandomNumberFactory(
				new double[] { 0.1 },
		//@formatter:off
				new int[]{NUMBER_OF_STARS,
// stars
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
				}));
		//@formatter:on
		Quadrant q = setup.makeQuadrant(map, 1, 2);
		assertEquals(5, q.getStars().size());

		assertEquals(Location.location(2, 3), q.getStars().get(0).getLocation());
		assertEquals("Class B star", q.getStars().get(0).getName());
		assertEquals("star star-class-b", q.getStars().get(0).getCss());

		assertEquals(Location.location(1, 1), q.getStars().get(1).getLocation());
		assertEquals("Class A star", q.getStars().get(1).getName());
		assertEquals("star star-class-a", q.getStars().get(1).getCss());

		assertEquals(Location.location(3, 1), q.getStars().get(2).getLocation());
		assertEquals("Class O star", q.getStars().get(2).getName());
		assertEquals("star star-class-o", q.getStars().get(2).getCss());

		assertEquals(Location.location(0, 1),q.getStars().get(3).getLocation());
		assertEquals("Class A star",q.getStars().get(3).getName());
		assertEquals("star star-class-a", q.getStars().get(3).getCss());

		assertEquals(Location.location(7, 0),q.getStars().get(4).getLocation());
		assertEquals("Class F star",q.getStars().get(4).getName());
		assertEquals("star star-class-f", q.getStars().get(4).getCss());
		
		assertEquals(NUMBER_OF_KLINGONS+1,q.getKlingons().size());
		
		List<Klingon> klingons = new ArrayList<>(q.getKlingons());
		assertEquals(Location.location(1, 5), klingons.get(0).getLocation());
		assertEquals("a Klingon raider",klingons.get(0).getName());

		assertEquals(Location.location(2, 2), klingons.get(1).getLocation());
		assertEquals("a Bird-of-prey",klingons.get(1).getName());

		assertEquals(Location.location(3, 2), klingons.get(2).getLocation());
		assertEquals("a Klingon raider",klingons.get(2).getName());
	}
}
