package superstartrek.client.activities.sector.contextmenu;

import superstartrek.client.bus.BaseHandler;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;

public interface SectorSelectedHandler extends BaseHandler{

	void onSectorSelected(Location sector, Quadrant quadrant, int screenX, int screenY);
}
