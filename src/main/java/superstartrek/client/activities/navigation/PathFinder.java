package superstartrek.client.activities.navigation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import superstartrek.client.activities.navigation.astar.AStar;
import superstartrek.client.activities.navigation.astar.Node;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;

public class PathFinder {

	Quadrant quadrant;
	
	public PathFinder(Quadrant quadrant) {
		this.quadrant = quadrant;
	}
	
	public List<Location> findPathBetween(Location from, Location to){
		AStar astar = new AStar(8, 8, new Node(from.getY(), from.getX()), new Node(to.getY(), to.getX()));
		Set<Location> obstacles = new HashSet<>();
		if (quadrant.getStarBase()!=null)
			obstacles.add(quadrant.getStarBase().getLocation());
		obstacles.addAll(StarMap.locations(quadrant.getKlingons()));
		obstacles.addAll(StarMap.locations(quadrant.getStars()));
		obstacles.remove(from);
		obstacles.remove(to);
		int[][] blocks = new int[obstacles.size()][2];
		int index=0;
		for (Location location:obstacles) {
			blocks[index++] = new int[] {location.getY(), location.getX()};
		}
		astar.setBlocks(blocks);
		List<Node> path = astar.findPath();
		List<Location> lPath = new ArrayList<>();
		for (Node node:path)
			lPath.add(Location.location(node.getCol(), node.getRow()));
		return lPath;
	}
}
