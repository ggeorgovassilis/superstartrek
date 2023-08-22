package superstartrek.client.activities.sector.contextmenu;

import superstartrek.client.eventbus.EventHandler;
import superstartrek.client.space.Location;
import superstartrek.client.space.Quadrant;

public interface SectorSelectedHandler extends EventHandler{

	void onSectorSelected(Location sector, Quadrant quadrant, int screenX, int screenY);
}
