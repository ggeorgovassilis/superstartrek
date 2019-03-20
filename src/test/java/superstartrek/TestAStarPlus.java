package superstartrek;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;

import superstartrek.client.activities.navigation.astar.AStar;
import superstartrek.client.activities.navigation.astar.Node;
import superstartrek.client.activities.navigation.astarplus.AStarPlus;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Star;
import superstartrek.client.model.StarMap;
public class TestAStarPlus {
	
	@Test
	public void testAStarPlusPerformance() {
		final int TURNS = 50000;
		final int TURNS_WITH_SAME_MAP = 500;
		Random random = new Random(0);
		long time = -System.currentTimeMillis();
		for (int i=0;i<TURNS;i++) {
			Quadrant q = new Quadrant("", 0, 0);
			StarMap map = new StarMap();
			Location from = Location.location(random.nextInt(8), random.nextInt(8));
			Location to = Location.location(random.nextInt(8), random.nextInt(8));
			int[][] blocksArray = new int[random.nextInt(32)][2];
			for (int l=0;l<blocksArray.length;l++) {
				int x = random.nextInt(8);
				int y = random.nextInt(8);
				blocksArray[l][0] = y;
				blocksArray[l][1] = x;
				q.getStars().add(new Star(x, y, false));
			}
			
			for (int innerTurn = 0; innerTurn<TURNS_WITH_SAME_MAP;innerTurn++) {
				AStarPlus asp = new AStarPlus();
				List<Location> pathAsp = asp.findPathBetween(from, to, q, map);
			}

		}
		time += System.currentTimeMillis();
		System.out.println(((double)time/(double)TURNS)+" ms per turn");
	}
	
	void printMap(int[][] blocksArray, Location from, Location to, List<Location> path) {
		char matrix[][] = new char[8][8];
		for (int l=0;l<blocksArray.length;l++) {
			int y = blocksArray[l][0];
			int x = blocksArray[l][1];
			matrix[x][y] = '#';
		}
		for (Location loc:path)
			matrix[loc.getX()][loc.getY()] = '*';
		matrix[from.getX()][from.getY()]='S';
		matrix[to.getX()][to.getY()]='E';
		for (int y=0;y<8;y++) {
			for (int x=0;x<8;x++)
				System.out.print(matrix[x][y]==(char)0?' ':matrix[x][y]);
			System.out.println();
		}
	}
	
	protected void checkPlausibility(Location from, Location to, List<Location> expectedSolution, List<Location> solutionToCheck) {
		if (expectedSolution.isEmpty()) {
			assertTrue(solutionToCheck.isEmpty());
			return;
		}
		assertEquals(expectedSolution.size()-1,solutionToCheck.size());
		if (expectedSolution.size()==1)
			return;
		assertEquals(from, solutionToCheck.get(0));
		Location p = solutionToCheck.get(0);
		for (Location l:solutionToCheck) {
			//astarPlus actually sometimes finds a better solution than a*
			assertTrue(StarMap.distance(p, l)<2);
			p = l;
		}
	}

	@Test
	public void testAStarPlus() {
		final int TURNS = 1000;
		Random random = new Random();
		for (int i=0;i<TURNS;i++) {
			Quadrant q = new Quadrant("", 0, 0);
			StarMap map = new StarMap();
			Location from = Location.location(random.nextInt(8), random.nextInt(8));
			Location to = Location.location(random.nextInt(8), random.nextInt(8));
			AStar astar = new AStar(8, 8, new Node(from.getY(), from.getX()), new Node(to.getY(), to.getX()));
			int[][] blocksArray = new int[random.nextInt(32)][2];
			for (int l=0;l<blocksArray.length;l++) {
				int x = random.nextInt(8);
				int y = random.nextInt(8);
				blocksArray[l][0] = y;
				blocksArray[l][1] = x;
				q.getStars().add(new Star(x, y, false));
			}
			astar.setBlocks(blocksArray);

			System.out.println(from+" -> "+to);
			List<Node> pathAStar = astar.findPath();
			List<Location> expectedSolution = new ArrayList<Location>(pathAStar.size());
			for (Node n:pathAStar)
				expectedSolution.add(Location.location(n.getCol(), n.getRow()));
			System.out.println("a*  "+pathAStar);
			printMap(blocksArray,from,to, expectedSolution);
			
			AStarPlus asp = new AStarPlus();
			List<Location> pathAsp = asp.findPathBetween(from, to, q, map);
			System.out.println("asp "+pathAsp);
			printMap(blocksArray,from,to, pathAsp);
			
			checkPlausibility(from, to, expectedSolution, pathAsp);
		}
	}
}
