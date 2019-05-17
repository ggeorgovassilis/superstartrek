package superstartrek.client.activities.navigation;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import superstartrek.client.bus.BaseHandler;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;

public interface EnterpriseWarpedHandler extends BaseHandler{

	void onEnterpriseWarped(Enterprise enterprise, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo);
}
