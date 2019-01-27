package superstartrek.client.activities.navigation;

import com.google.gwt.event.shared.EventHandler;

import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Thing;

public interface ThingMovedHandler extends EventHandler{

	public void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo);
}
