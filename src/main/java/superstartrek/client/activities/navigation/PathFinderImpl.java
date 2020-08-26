package superstartrek.client.activities.navigation;

import java.util.List;
import superstartrek.client.activities.navigation.astarplus.AStarPlus;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;

public class PathFinderImpl implements PathFinder{

	@Override
	public List<Location> findPathBetween(Location from, Location to, Quadrant quadrant, StarMap map){
		//TODO: ideas to speed this up
		/* This method is called frequently: once per turn per klingon in the quadrant and is thus a hotspot.
	     */
		
		AStarPlus astar = new AStarPlus();
		List<Location> path = astar.findPathBetween(from, to, quadrant, map, 100);
		return path;
	}
}
