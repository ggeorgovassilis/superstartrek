package superstartrek.client.activities.sector.scan;

import superstartrek.client.bus.EventHandler;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;

public interface ScanSectorHandler extends EventHandler{

	void scanSector(Location location, Quadrant quadrant);
}
