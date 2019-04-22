package superstartrek;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gwt.event.shared.SimpleEventBus;
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
import superstartrek.client.model.Star.StarClass;

/*
 * A* is a reference implementation used to validate that a*+ works ok
 */
public class TestAStarPlus {
	
	@After
	public void cleanup() {
		Application.set(null);
	}


	@Ignore //too slow for regular builds
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
				q.getStars().add(new Star(x, y, false, StarClass.A));
			}

			for (int innerTurn = 0; innerTurn < TURNS_WITH_SAME_MAP; innerTurn++) {
				AStarPlus asp = new AStarPlus();
				List<Location> pathAsp = asp.findPathBetween(from, to, q, map);
				assertTrue(pathAsp.size()>=0); // useless check, but mutes Eclipse warning about pathAsp not used
			}

		}
		time += System.currentTimeMillis();
		System.out.println(((double) time / (double) TURNS) + " ms per turn");
	}

	public static String printMap(int[][] blocksArray, Location from, Location to, List<Location> path) {
		StringBuffer sb = new StringBuffer();
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
				sb.append(matrix[x][y] == (char) 0 ? ' ' : matrix[x][y]);
			sb.append("\n");
		}
		return sb.toString();
	}

	protected void checkPlausibility(Location from, Location to, List<Location> expectedSolution,
			List<Location> solutionToCheck, String log) {
		Location last = from;
		//check some corner cases:
		
		//1. from == to
		if (from.equals(to)) {
			assertTrue(log, solutionToCheck.isEmpty());
			return;
		}
		//2. expectedSolution is empty even though there is a path
		//a* sometimes doesn't find a path even if there is one (which a*+ finds).
		//this means that sometimes expectedSolution is empty even though it shouldn't
		if (solutionToCheck.isEmpty()) {
			assertTrue(log, expectedSolution.isEmpty());
			return;
		}
		//3. "to" is last step in path
		assertEquals(log, to, solutionToCheck.get(solutionToCheck.size()-1));
		
		//4. the expected solution is about the same size as the one we found; a*+ walks diagonally, so its solutions are generally better, hence
		//expectedSolution is an upper bound
		if (!expectedSolution.isEmpty())
			assertTrue(log, expectedSolution.size() >= solutionToCheck.size());
		
		//5. there are no "leaps"; every square in the path is next to the previous
		for (Location current:solutionToCheck) {
			assertTrue(log, StarMap.within_distance(current, last,2));
			last = current;
		}
		
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
			Application app = new Application();
			app.events = new SimpleEventBus();
			map.enterprise = new Enterprise(app, map);
			Location from = Location.location(random.nextInt(8), random.nextInt(8));
			Location to = Location.location(random.nextInt(8), random.nextInt(8));
			AStar astar = new AStar(8, 8, new Node(from.getY(), from.getX()), new Node(to.getY(), to.getX()));
			int[][] blocksArray = new int[random.nextInt(32)][2];
			for (int l = 0; l < blocksArray.length; l++) {
				int x = random.nextInt(8);
				int y = random.nextInt(8);
				blocksArray[l][0] = y;
				blocksArray[l][1] = x;
				q.getStars().add(new Star(x, y, false, StarClass.A));
			}
			astar.setBlocks(blocksArray);

			List<Node> pathAStar = astar.findPath();
			List<Location> expectedSolution = new ArrayList<Location>(pathAStar.size());
			for (Node n : pathAStar)
				expectedSolution.add(Location.location(n.getCol(), n.getRow()));

			AStarPlus asp = new AStarPlus();
			List<Location> pathAsp = asp.findPathBetween(from, to, q, map);
			String log = "--------------\n";
			log+=(from + " -> " + to+"\n");
			log+=("a*  " + expectedSolution+"\n");
			log+=printMap(blocksArray, from, to, expectedSolution)+"\n";
			log+=("a*+ " + pathAsp+"\n");
			log+=printMap(blocksArray, from, to, pathAsp)+"\n";

			checkPlausibility(from, to, expectedSolution, pathAsp, log);
			
			Application.set(null);

		}
	}

	@Test
	public void testAStarPlus_2() {
		Quadrant q = new Quadrant("", 0, 0);
		StarMap map = new StarMap();
		Location from = Location.location(1, 3);
		Location to = Location.location(2, 7);
		Application app = new Application();
		app.events = new com.google.gwt.event.shared.testing.CountingEventBus();
		map.enterprise = new Enterprise(Application.get(), map);
		//e.setLocation(to);
		//map.enterprise = e;
	//	e.setQuadrant(q);
		Klingon k = new Klingon(ShipClass.BirdOfPrey);
		k.setLocation(from);
		q.getKlingons().add(k);
		AStarPlus asp = new AStarPlus();
		List<Location> pathAsp = asp.findPathBetween(from, to, q, map);
		System.out.println("asp " + pathAsp);
	}
}
