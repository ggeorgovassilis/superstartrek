package astar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import superstartrek.BaseTest;
import superstartrek.client.Application;
import superstartrek.client.activities.navigation.astarplus.AStarPlus;
import superstartrek.client.space.Constants;
import superstartrek.client.space.Location;
import superstartrek.client.space.Quadrant;
import superstartrek.client.space.Star;
import superstartrek.client.space.StarMap;
import superstartrek.client.space.Star.StarClass;
import superstartrek.client.vessels.Enterprise;
import superstartrek.client.vessels.Klingon;
import superstartrek.client.vessels.Klingon.ShipClass;

/*
 * A* is a reference implementation used to validate that a*+ works ok
 */
public class TestAStarPlus extends BaseTest {
	
	int failedPaths = 0;

	@Ignore //test too slow for regular builds
	@Test
	public void testAStarPlusPerformance() {
		final long TEST_DURATION_MS = 5000;
		final int TURNS_WITH_SAME_MAP = 500;
		Random random = new Random(0);
		long time = -System.currentTimeMillis();
		int turns = 0;
		while (time+System.currentTimeMillis()<TEST_DURATION_MS) {
			Quadrant q = new Quadrant("", 0, 0);
			Location from = Location.location(random.nextInt(Constants.SECTORS_EDGE),
					random.nextInt(Constants.SECTORS_EDGE));
			Location to = Location.location(random.nextInt(Constants.SECTORS_EDGE),
					random.nextInt(Constants.SECTORS_EDGE));
			int obstacles = random.nextInt(32);
			for (int l = 0; l < obstacles; l++) {
				int x = random.nextInt(Constants.SECTORS_EDGE);
				int y = random.nextInt(Constants.SECTORS_EDGE);
				q.add(new Star(Location.location(x, y), StarClass.A));
			}

			for (int innerTurn = 0; innerTurn < TURNS_WITH_SAME_MAP; innerTurn++) {
				AStarPlus asp = new AStarPlus();
				List<Location> pathAsp = asp.findPathBetween(from, to, q, 100);
				assertTrue(pathAsp.size() >= 0); // useless check, but mutes Eclipse warning about pathAsp not used
			}
			turns++;
		}
		time += System.currentTimeMillis();
		System.out.println(((double) time / (double) turns) + " ms per turn");
	}

	public static String printMap(Quadrant quadrant, Location from, Location to, List<Location> path) {
		StringBuffer sb = new StringBuffer();
		char matrix[][] = new char[Constants.SECTORS_EDGE][Constants.SECTORS_EDGE];
		for (int y = 0; y < Constants.SECTORS_EDGE; y++) 
			for (int x = 0; x < Constants.SECTORS_EDGE; x++)
				matrix[x][y] = ' ';
		quadrant.doWithThings(t->matrix[t.getLocation().x][t.getLocation().y]='#');
		for (Location loc : path)
			matrix[loc.x][loc.y] = '*';
		matrix[from.x][from.y] = 'S';
		matrix[to.x][to.y] = 'E';
		sb.append("|");
		for (int y = 0; y < Constants.SECTORS_EDGE; y++) {
			for (int x = 0; x < Constants.SECTORS_EDGE; x++)
				sb.append(matrix[x][y]);
			sb.append("\n|");
		}
		return sb.toString();
	}

	protected void checkPlausibility(Location from, Location to, List<Location> solutionToCheck, Quadrant q,
			String log) {
		Location previousStep = from;
		int count[] = new int[1];
		q.doWithThings(t->count[0]++);
		
		if (StarMap.within_distance(from, to, 1.5)) {
			assertTrue(solutionToCheck.isEmpty());
			return;
		}

		//There is currently no reliable way to check whether an existing solution was missed.
		//However there are some heuristics:
		//
		if (solutionToCheck.isEmpty()) {
			//1. sectors can't be adjacent
			assertFalse(StarMap.within_distance(from, to, 1.5));
			//2. enough obstacles are present
			assertTrue(count[0]>2);
			return;
		}
		//The rationale behind this length check is: the more obstacles, the longer the path.
		assertTrue(log, solutionToCheck.size() < Constants.SECTORS_EDGE+count[0]);
		assertTrue(StarMap.distance(from, to)+" "+log, 1.5*solutionToCheck.size()+2>StarMap.distance(from, to));
		for (Location l : solutionToCheck) {
			String errorMessage = l.toString() + "\n" + log;
			assertTrue(errorMessage, StarMap.distance(l, previousStep) < 2);
			assertNull(errorMessage, q.findThingAt(l));
			previousStep = l;
		}
	}

	/*
	 * surprisingly, a* reference implementation fails with this map, which a*+
	 * solves:
	 * 
	 * # E # # * ## #* # * # #S # # # ## # ## #
	 * 
	 */
	@Test
	public void testAStarPlus() {
		final int TURNS = 3000;
		Random random = new Random(0);
		for (int i = 0; i < TURNS; i++) {
			Quadrant q = new Quadrant("", 0, 0);
			StarMap map = new StarMap();
			Application.set(null);
			Application app = new Application();
			map.enterprise = new Enterprise(app, map);
			Application.set(app);
			app.starMap = map;
			Location from = Location.location(random.nextInt(Constants.SECTORS_EDGE),
					random.nextInt(Constants.SECTORS_EDGE));
			Location to = Location.location(random.nextInt(Constants.SECTORS_EDGE),
					random.nextInt(Constants.SECTORS_EDGE));
			int obstacles = random.nextInt(32);
			for (int l = 0; l < obstacles; l++) {
				int x = random.nextInt(Constants.SECTORS_EDGE);
				int y = random.nextInt(Constants.SECTORS_EDGE);
				q.add(new Star(Location.location(x, y), StarClass.A));
			}

			AStarPlus asp = new AStarPlus();
			List<Location> pathAsp = asp.findPathBetween(from, to, q, 100);
			String log = "--------------\n";
			log += ("a*+ " + pathAsp + "\n");
			log += printMap(q, from, to, pathAsp) + "\n";

			checkPlausibility(from, to, pathAsp, q, log);

			Application.set(null);

		}
	}

	/*
	 * Same as testAStarPlus but with trimming steps
	 */
	@Test
	public void testAStarPlus_abbreviated() {
		final int TURNS = 3000;
		Random random = new Random(0);
		for (int i = 0; i < TURNS; i++) {
			Quadrant q = new Quadrant("", 0, 0);
			StarMap map = new StarMap();
			Application.set(null);
			Application app = new Application();
			map.enterprise = new Enterprise(app, map);
			Application.set(app);
			app.starMap = map;
			Location from = Location.location(random.nextInt(Constants.SECTORS_EDGE),
					random.nextInt(Constants.SECTORS_EDGE));
			Location to = Location.location(random.nextInt(Constants.SECTORS_EDGE),
					random.nextInt(Constants.SECTORS_EDGE));
			int obstacles = random.nextInt(32);
			for (int l = 0; l < obstacles; l++) {
				int x = random.nextInt(Constants.SECTORS_EDGE);
				int y = random.nextInt(Constants.SECTORS_EDGE);
				q.add(new Star(Location.location(x, y), StarClass.A));
			}

			AStarPlus asp = new AStarPlus();
			List<Location> pathAsp = asp.findPathBetween(from, to, q, Constants.KLINGON_MAX_SECTOR_SPEED);
			String log = "--------------\n";
			log += ("a*+ " + pathAsp + "\n");
			log += printMap(q, from, to, pathAsp) + "\n";

			AStarPlus reference = new AStarPlus();
			List<Location> fullPath = reference.findPathBetween(from, to, q, 100);
			for (int s=0;s<Math.min(Constants.KLINGON_MAX_SECTOR_SPEED, fullPath.size());s++)
				assertEquals(log, fullPath.get(s), pathAsp.get(s));

			Application.set(null);

		}
	}

	@Test
	public void testAStarPlus_2() {
		Quadrant q = new Quadrant("", 0, 0);
		StarMap map = new StarMap();
		Location from = Location.location(1, 3);
		Location to = Location.location(2, 7);
		map.enterprise = new Enterprise(Application.get(), map);
		// e.setLocation(to);
		// map.enterprise = e;
		// e.setQuadrant(q);
		Klingon k = new Klingon(ShipClass.BirdOfPrey);
		k.setLocation(from);
		q.add(k);
		AStarPlus asp = new AStarPlus();
		List<Location> pathAsp = asp.findPathBetween(from, to, q, 100);
		System.out.println("asp " + pathAsp);
	}
}
