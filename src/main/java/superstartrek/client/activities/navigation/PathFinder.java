package superstartrek.client.activities.navigation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.editor.client.Editor.Path;

import superstartrek.client.activities.navigation.astar.AStar;
import superstartrek.client.activities.navigation.astar.Node;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Thing;

public class PathFinder {

	Quadrant quadrant;
	
	public PathFinder(Quadrant quadrant) {
		this.quadrant = quadrant;
	}
	
	List<Location> locations(List<? extends Thing> things){
		List<Location> locations = new ArrayList<>();
		for (Thing t:things)
			locations.add(t.getLocation());
		return locations;
	}
	
	public List<Location> findPathBetween(Location from, Location to){
		AStar astar = new AStar(8, 8, new Node(from.getY(), from.getX()), new Node(to.getY(), to.getX()));
		List<Location> obstacles = new ArrayList<>();
		if (quadrant.getStarBase()!=null)
			obstacles.add(quadrant.getStarBase().getLocation());
		obstacles.addAll(locations(quadrant.getKlingons()));
		obstacles.addAll(locations(quadrant.getStars()));
		obstacles.remove(from);
		obstacles.remove(to);
		int[][] blocks = new int[obstacles.size()][2];
		for (int i=0;i<obstacles.size();i++) {
			Location l = obstacles.get(i);
			blocks[i] = new int[] {l.getY(), l.getX()};
		}
		astar.setBlocks(blocks);
		List<Node> path = astar.findPath();
		List<Location> lPath = new ArrayList<>();
		for (Node node:path)
			lPath.add(new Location(node.getCol(), node.getRow()));
		return lPath;
	}
}
