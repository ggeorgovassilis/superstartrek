package superstartrek.client.activities.sector.scan;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import superstartrek.client.bus.BaseHandler;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;

public interface ScanSectorHandler extends BaseHandler{

	void scanSector(Location location, Quadrant quadrant);
}
