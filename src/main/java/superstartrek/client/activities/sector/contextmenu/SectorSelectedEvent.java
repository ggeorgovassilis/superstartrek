package superstartrek.client.activities.sector.contextmenu;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.GwtEvent;

import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;

public class SectorSelectedEvent extends GwtEvent<SectorSelectedHandler>{

	public static Type<SectorSelectedHandler> TYPE = new Type<SectorSelectedHandler>();
	public final Location sector;
	public final Quadrant quadrant;
	public final int screenX;
	public final int screenY;
	
	public SectorSelectedEvent(Location sector, Quadrant quadrant, int screenX, int screenY) {
		this.sector = Location.location(sector.getX(), sector.getY());
		this.quadrant = quadrant;
		this.screenX = screenX;
		this.screenY = screenY;
	}

	@Override
	public Type<SectorSelectedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SectorSelectedHandler handler) {
		handler.onSectorSelected(this);
	}
	
	public Location getSector() {
		return sector;
	}
	
	public Quadrant getQuadrant() {
		return quadrant;
	}

}

