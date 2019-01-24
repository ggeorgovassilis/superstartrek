package superstartrek.client.activities.sector.scan;

import com.google.gwt.event.shared.GwtEvent;

import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;

public class ScanSectorEvent extends GwtEvent<ScanSectorHandler> {

	public static Type<ScanSectorHandler> TYPE = new Type<ScanSectorHandler>();

	protected final Location location;
	protected final Quadrant quadrant;
	
	public ScanSectorEvent(Location location, Quadrant quadrant) {
		this.location = location;
		this.quadrant = quadrant;
	}
	
	@Override
	public Type<ScanSectorHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ScanSectorHandler handler) {
		handler.scanSector(this);
	}
	
	public Location getLocation() {
		return location;
	}
	
	public Quadrant getQuadrant() {
		return quadrant;
	}

}