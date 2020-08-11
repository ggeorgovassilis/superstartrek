package superstartrek.client.activities.sector.contextmenu;

import superstartrek.client.bus.EventHandler;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;

public interface SectorSelectedHandler extends EventHandler{

	void onSectorSelected(Location sector, Quadrant quadrant, int screenX, int screenY);
}
