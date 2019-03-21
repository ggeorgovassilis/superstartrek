package superstartrek.client.activities.navigation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import superstartrek.client.activities.navigation.astarplus.AStarPlus;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;

public class PathFinderImpl implements PathFinder{

	int[][] getObstacles(Location from, Location to, Quadrant quadrant){
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
			blocks[index][0] = location.getY();
			blocks[index][1] = location.getX();
			index++;
		}
		return blocks;
	}
	
	@Override
	public List<Location> findPathBetween(Location from, Location to, Quadrant quadrant, StarMap map){
		//TODO: ideas to speed this up
		/* This method is called frequently: once per turn per klingon in the quadrant and is thus a hotspot.
	     */
		
		AStarPlus astar = new AStarPlus();
		List<Location> path = astar.findPathBetween(from, to, quadrant, map);
		return path;
	}
}
