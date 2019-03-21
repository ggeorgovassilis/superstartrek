package superstartrek;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;

import com.google.web.bindery.event.shared.testing.CountingEventBus;

import astar.AStar;
import astar.Node;
import superstartrek.client.Application;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.Klingon.ShipClass;
import superstartrek.client.activities.navigation.astarplus.AStarPlus;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Star;
import superstartrek.client.model.StarMap;

/*
 * A* is a reference implementation used to validate that a*+ works ok
 */
public class TestAStarPlus {

//	@Ignore //too slow for regular builds
	@Test
	public void testAStarPlusPerformance() {
		final int TURNS = 50000;
		final int TURNS_WITH_SAME_MAP = 500;
		Random random = new Random(0);
		long time = -System.currentTimeMillis();
		for (int i = 0; i < TURNS; i++) {
			Quadrant q = new Quadrant("", 0, 0);
			StarMap map = new StarMap();
			Location from = Location.location(random.nextInt(8), random.nextInt(8));
			Location to = Location.location(random.nextInt(8), random.nextInt(8));
			int[][] blocksArray = new int[random.nextInt(32)][2];
			for (int l = 0; l < blocksArray.length; l++) {
				int x = random.nextInt(8);
				int y = random.nextInt(8);
				blocksArray[l][0] = y;
				blocksArray[l][1] = x;
				q.getStars().add(new Star(x, y, false));
			}

			for (int innerTurn = 0; innerTurn < TURNS_WITH_SAME_MAP; innerTurn++) {
				AStarPlus asp = new AStarPlus();
				List<Location> pathAsp = asp.findPathBetween(from, to, q, map);
			}

		}
		time += System.currentTimeMillis();
		System.out.println(((double) time / (double) TURNS) + " ms per turn");
	}

	public static void printMap(int[][] blocksArray, Location from, Location to, List<Location> path) {
		char matrix[][] = new char[8][8];
		for (int l = 0; l < blocksArray.length; l++) {
			int y = blocksArray[l][0];
			int x = blocksArray[l][1];
			matrix[x][y] = '#';
		}
		for (Location loc : path)
			matrix[loc.getX()][loc.getY()] = '*';
		matrix[from.getX()][from.getY()] = 'S';
		matrix[to.getX()][to.getY()] = 'E';
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++)
				System.out.print(matrix[x][y] == (char) 0 ? ' ' : matrix[x][y]);
			System.out.println();
		}
	}

	protected void checkPlausibility(Location from, Location to, List<Location> expectedSolution,
			List<Location> solutionToCheck) {
		if (expectedSolution.isEmpty()) {
			//a*+ (incorrectly) returns source square as step; but so does a*, so that's ok
			//a*+ also (correctly) returns target square as step, which a* doesn't. so we allow that here for 
			//leniency in the case of a path between two adjacent sectors
			assertTrue(solutionToCheck.size()<=2);
			return;
		}
		assertTrue(Math.abs(expectedSolution.size() - solutionToCheck.size()) < 3);
		if (expectedSolution.size() == 1)
			return;
		assertEquals(from, solutionToCheck.get(0));
		Location p = solutionToCheck.get(0);
		for (Location l : solutionToCheck) {
			// astarPlus actually sometimes finds a better solution than a*
			assertTrue(StarMap.distance(p, l) < 2);
			p = l;
		}
		assertTrue(StarMap.distance(to, solutionToCheck.get(solutionToCheck.size() - 1)) < 2);
	}

	/* surprisingly, a* reference implementation fails with this map, which a*+ solves: 
	 
    #  E
# #   * 
##  #*  
  # *  #
    #S #
  # #   
##     #
 ##  #   
	 
	 */
	@Test
	public void testAStarPlus() {
		final int TURNS = 1000;
		Random random = new Random(0);
		for (int i = 0; i < TURNS; i++) {
			Quadrant q = new Quadrant("", 0, 0);
			StarMap map = new StarMap();
			Location from = Location.location(random.nextInt(8), random.nextInt(8));
			Location to = Location.location(random.nextInt(8), random.nextInt(8));
			AStar astar = new AStar(8, 8, new Node(from.getY(), from.getX()), new Node(to.getY(), to.getX()));
			int[][] blocksArray = new int[random.nextInt(32)][2];
			for (int l = 0; l < blocksArray.length; l++) {
				int x = random.nextInt(8);
				int y = random.nextInt(8);
				blocksArray[l][0] = y;
				blocksArray[l][1] = x;
				q.getStars().add(new Star(x, y, false));
			}
			astar.setBlocks(blocksArray);

			List<Node> pathAStar = astar.findPath();
			List<Location> expectedSolution = new ArrayList<Location>(pathAStar.size());
			for (Node n : pathAStar)
				expectedSolution.add(Location.location(n.getCol(), n.getRow()));

			AStarPlus asp = new AStarPlus();
			List<Location> pathAsp = asp.findPathBetween(from, to, q, map);
			System.out.println("--------------");
			System.out.println(from + " -> " + to);
			System.out.println("a*  " + expectedSolution);
			printMap(blocksArray, from, to, expectedSolution);
			System.out.println("a*+ " + pathAsp);
			printMap(blocksArray, from, to, pathAsp);

			checkPlausibility(from, to, expectedSolution, pathAsp);
		}
	}

	@Test
	public void testAStarPlus_2() {
		Quadrant q = new Quadrant("", 0, 0);
		StarMap map = new StarMap();
		Location from = Location.location(1, 3);
		Location to = Location.location(2, 7);
		Application.get().events = new com.google.gwt.event.shared.testing.CountingEventBus();
		//Enterprise e = new Enterprise(Application.get());
		//e.setLocation(to);
		//map.enterprise = e;
	//	e.setQuadrant(q);
		Klingon k = new Klingon(ShipClass.BirdOfPrey);
		k.setQuadrant(q);
		k.setLocation(from);
		q.getKlingons().add(k);
		AStarPlus asp = new AStarPlus();
		List<Location> pathAsp = asp.findPathBetween(from, to, q, map);
		System.out.println("asp " + pathAsp);
	}
}
