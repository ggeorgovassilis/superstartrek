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
		//TODO: ideas to speed this up
		/* This method is called frequently: once per turn per klingon in the quadrant and is thus a hotspot.
	     */
		AStar astar = new AStar(8, 8, new Node(from.getY(), from.getX()), new Node(to.getY(), to.getX()));
		Set<Location> obstacles = new HashSet<>();
		if (quadrant.getStarBase()!=null)
			obstacles.add(quadrant.getStarBase().getLocation());
		obstacles.addAll(StarMap.locations(quadrant.getKlingons()));
		obstacles.addAll(StarMap.locations(quadrant.getStars()));
		obstacles.remove(from);
		obstacles.remove(to);
		
		/*what we really want is the 2D blocks array; the obstacles list is a temporary collection. Can we do without the obstacles list?
		 */
		int[][] blocks = new int[obstacles.size()][2];
		int index=0;
		for (Location location:obstacles) {
			blocks[index++] = new int[] {location.getY(), location.getX()};
		}
		
		astar.setBlocks(blocks);
		
		//maybe rewrite the a* implementation to directly use our data representation instead of its own nodes?
		List<Node> path = astar.findPath();
		List<Location> lPath = new ArrayList<>();
		for (Node node:path)
			lPath.add(Location.location(node.getCol(), node.getRow()));
		return lPath;
	}
}
