package superstartrek.client.activities.navigation;

import superstartrek.client.eventbus.EventHandler;
import superstartrek.client.space.Location;
import superstartrek.client.space.Quadrant;
import superstartrek.client.space.StarBase;
import superstartrek.client.space.Thing;
import superstartrek.client.vessels.Enterprise;

public interface NavigationHandler extends EventHandler {

	default void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
	}

	default void onEnterpriseDocked(Enterprise enterprise, StarBase starBase, int itemsRepaired, int torpedosRestocked, int antimatterRefuelled) {
	}

}
