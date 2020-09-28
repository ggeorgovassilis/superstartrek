package superstartrek.client.activities.navigation;

import superstartrek.client.bus.EventHandler;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarBase;
import superstartrek.client.model.Thing;

public interface NavigationHandler extends EventHandler {

	default void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
	}

	default void onEnterpriseDocked(Enterprise enterprise, StarBase starBase, int itemsRepaired, int torpedosRestocked, int antimatterRefuelled) {
	}

}
