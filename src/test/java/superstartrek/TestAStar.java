package superstartrek;

import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import superstartrek.client.activities.navigation.PathFinder;
import superstartrek.client.activities.navigation.PathFinderImpl;
import superstartrek.client.activities.navigation.astar.AStar;
import superstartrek.client.activities.navigation.astar.Node;

@Ignore
public class TestAStar {
	
	

	@Test
	public void testAStar() {
		final int TURNS = 1000;
		Random random = new Random();
		for (int i=0;i<TURNS;i++) {
			AStar astar = new AStar(8, 8, new Node(random.nextInt(8), random.nextInt(8)), new Node(random.nextInt(8), random.nextInt(8)));
			int[][] blocksArray = new int[random.nextInt(32)][2];
			for (int l=0;l<blocksArray.length;l++) {
				blocksArray[l][0] = random.nextInt(8);
				blocksArray[l][1] = random.nextInt(8);
			}
			astar.setBlocks(blocksArray);
			List<Node> path = astar.findPath();
			System.out.println(path);
		}
	}
}
