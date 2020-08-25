package superstartrek.client.activities.navigation;

import java.util.List;

import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;

public interface PathFinder {

	/**
	 * Returns a list of locations one can traverse in the direction from "from" to "to".
	 * Path will avoid obstacles. Any "Thing" instance is considered an obstacle.
	 * @param from Location to start path from
	 * @param to Location to end path at
	 * @param quadrant Quadrant the path is in
	 * @param map 
	 * @return
	 */
	List<Location> findPathBetween(Location from, Location to, Quadrant quadrant, StarMap map);

}
