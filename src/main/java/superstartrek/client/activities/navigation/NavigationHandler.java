package superstartrek.client.activities.navigation;

import superstartrek.client.bus.BaseHandler;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarBase;
import superstartrek.client.model.Thing;

public interface NavigationHandler extends BaseHandler {

	default void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
	}

	default void onEnterpriseWarped(Enterprise enterprise, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
	}

	default void onEnterpriseDocked(Enterprise enterprise, StarBase starBase) {
	}

}
