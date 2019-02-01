package superstartrek.client.activities.navigation;

import com.google.gwt.event.shared.EventHandler;

import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;

public interface EnterpriseWarpedHandler extends EventHandler{

	void onEnterpriseWarped(Enterprise enterprise, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo);
}
