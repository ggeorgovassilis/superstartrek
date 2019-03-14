package superstartrek.client.activities.navigation;

import java.util.List;

import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;

public interface PathFinder {

	List<Location> findPathBetween(Location from, Location to, Quadrant quadrant);

}
