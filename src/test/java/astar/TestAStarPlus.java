package astar;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import superstartrek.BaseTest;
import superstartrek.client.Application;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.Klingon.ShipClass;
import superstartrek.client.activities.navigation.astarplus.AStarPlus;
import superstartrek.client.model.Constants;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Star;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Star.StarClass;

/*
 * A* is a reference implementation used to validate that a*+ works ok
 */
public class TestAStarPlus extends BaseTest {

	@Ignore // too slow for regular builds
	@Test
	public void testAStarPlusPerformance() {
		final int TURNS = 50000;
		final int TURNS_WITH_SAME_MAP = 500;
		Random random = new Random(0);
		long time = -System.currentTimeMillis();
		for (int i = 0; i < TURNS; i++) {
			Quadrant q = new Quadrant("", 0, 0);
			StarMap map = new StarMap();
			Location from = Location.location(random.nextInt(Constants.SECTORS_EDGE),
					random.nextInt(Constants.SECTORS_EDGE));
			Location to = Location.location(random.nextInt(Constants.SECTORS_EDGE),
					random.nextInt(Constants.SECTORS_EDGE));
			int obstacles = random.nextInt(32);
			for (int l = 0; l < obstacles; l++) {
				int x = random.nextInt(Constants.SECTORS_EDGE);
				int y = random.nextInt(Constants.SECTORS_EDGE);
				q.add(new Star(x, y, false, StarClass.A));
			}

			for (int innerTurn = 0; innerTurn < TURNS_WITH_SAME_MAP; innerTurn++) {
				AStarPlus asp = new AStarPlus();
				List<Location> pathAsp = asp.findPathBetween(from, to, q, map);
				assertTrue(pathAsp.size() >= 0); // useless check, but mutes Eclipse warning about pathAsp not used
			}

		}
		time += System.currentTimeMillis();
		System.out.println(((double) time / (double) TURNS) + " ms per turn");
	}

	public static String printMap(Quadrant quadrant, Location from, Location to, List<Location> path) {
		StringBuffer sb = new StringBuffer();
		char matrix[][] = new char[Constants.SECTORS_EDGE][Constants.SECTORS_EDGE];
		for (int y = 0; y < Constants.SECTORS_EDGE; y++) 
			for (int x = 0; x < Constants.SECTORS_EDGE; x++)
				matrix[x][y] = ' ';
		quadrant.doWithThings(t->matrix[t.getLocation().getX()][t.getLocation().getY()]='#');
		for (Location loc : path)
			matrix[loc.getX()][loc.getY()] = '*';
		matrix[from.getX()][from.getY()] = 'S';
		matrix[to.getX()][to.getY()] = 'E';
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
		//The rationale behind this length check is: the more obstacles, the longer the path.
		assertTrue(log, solutionToCheck.size() < Constants.SECTORS_EDGE+count[0]);
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
				q.add(new Star(x, y, false, StarClass.A));
			}

			AStarPlus asp = new AStarPlus();
			List<Location> pathAsp = asp.findPathBetween(from, to, q, map);
			String log = "--------------\n";
			log += ("a*+ " + pathAsp + "\n");
			log += printMap(q, from, to, pathAsp) + "\n";

			checkPlausibility(from, to, pathAsp, q, log);

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
		List<Location> pathAsp = asp.findPathBetween(from, to, q, map);
		System.out.println("asp " + pathAsp);
	}
}
