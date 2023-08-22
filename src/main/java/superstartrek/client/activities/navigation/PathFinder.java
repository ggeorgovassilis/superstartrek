package superstartrek.client.activities.navigation;

import java.util.List;

import superstartrek.client.space.Location;
import superstartrek.client.space.Quadrant;

public interface PathFinder {

	/**
	 * Returns a list of locations one can traverse in the direction from "from" to "to".
	 * Path will avoid obstacles. Any "Thing" instance is considered an obstacle.
	 * @param from Location to start path from
	 * @param to Location to end path at
	 * @return
	 */
	List<Location> findPathBetween(Location from, Location to);
	
	/**
	 * Paths will be found in this quadrant. Must be called before invoking {@link #findPathBetween(Location, Location)}
	 */
	void load(Quadrant quadrant);

}
