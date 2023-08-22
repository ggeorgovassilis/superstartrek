package superstartrek.client.activities.navigation;

import java.util.List;
import superstartrek.client.activities.navigation.astarplus.AStarPlus;
import superstartrek.client.space.Location;
import superstartrek.client.space.Quadrant;

public class PathFinderImpl implements PathFinder{
	
	Quadrant quadrant;
	
	@Override
	public List<Location> findPathBetween(Location from, Location to){
		//TODO: ideas to speed this up
		/* This method is called frequently: once per turn per klingon in the quadrant and is thus a hotspot.
	     */
		
		AStarPlus astar = new AStarPlus();
		List<Location> path = astar.findPathBetween(from, to, quadrant, 100);
		return path;
	}

	@Override
	public void load(Quadrant quadrant) {
		this.quadrant = quadrant;
	}
}
