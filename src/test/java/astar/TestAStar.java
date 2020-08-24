package astar;

import java.util.List;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import superstartrek.client.activities.navigation.astarplus.AStarPlus;
import superstartrek.client.model.Constants;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Star;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Star.StarClass;
@Ignore
public class TestAStar {

	@Test
	public void testAStar() {
		final int TURNS = 1000;
		Random random = new Random();
		for (int i=0;i<TURNS;i++) {
			Quadrant q = new Quadrant("", 0, 0);
			StarMap map = new StarMap();
			Location from = Location.location(random.nextInt(Constants.SECTORS_EDGE), random.nextInt(Constants.SECTORS_EDGE));
			Location to = Location.location(random.nextInt(Constants.SECTORS_EDGE), random.nextInt(Constants.SECTORS_EDGE));
			AStar astar = new AStar(Constants.SECTORS_EDGE, Constants.SECTORS_EDGE, new Node(from.getY(), from.getX()), new Node(to.getY(), to.getX()));
			int[][] blocksArray = new int[random.nextInt(32)][2];
			for (int l=0;l<blocksArray.length;l++) {
				int x = random.nextInt(Constants.SECTORS_EDGE);
				int y = random.nextInt(Constants.SECTORS_EDGE);
				blocksArray[l][0] = y;
				blocksArray[l][1] = x;
				q.add(new Star(x, y, false, StarClass.A));
			}
			astar.setBlocks(blocksArray);
			List<Node> pathAStar = astar.findPath();
			System.out.println("a*  "+pathAStar);
			
			AStarPlus asp = new AStarPlus();
			List<Location> pathAsp = asp.findPathBetween(from, to, q, map);
			System.out.println("asp "+pathAsp);
		}
	}
}
