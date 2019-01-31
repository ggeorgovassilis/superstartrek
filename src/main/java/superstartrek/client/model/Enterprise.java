package superstartrek.client.model;

import java.util.List;

import superstartrek.client.Application;
import superstartrek.client.activities.navigation.ThingMovedEvent;

public class Enterprise extends Thing{

	protected Application application;
	
	protected final Setting impulse = new Setting(3, 3);
	
	public Enterprise(Application app) {
		this.application = app;
		setName("NCC 1701 USS Enterprise");
		setSymbol("O=Ξ");
		setCss("enterprise");
	}
	
	public Setting getImpulse() {
		return impulse;
	}
	
	public void navigateTo(Location loc) {
		Location oldLoc = new Location(getX(), getY());
		if (StarMap.distance(oldLoc, loc)>getImpulse().getValue()) {
			application.message("Distance exceeds maximum impulse power "+getImpulse().getValue());
			return;
		}
		Thing thing = application.starMap.findThingAt(quadrant, loc.x, loc.y);
		if (thing!=null) {
			application.message("Destination is occupied");
			return;
		}
		List<Thing> things = application.starMap.findObstaclesInLine(quadrant, getX(), getY(), loc.getX(), loc.getY());
		if (things.size()>1) { //there's always at least 1 thing, the USS Enterprise
			application.message("Path isn't clear");
			return;
		}
		this.setLocation(loc);
		application.events.fireEvent(new ThingMovedEvent(this, getQuadrant(), oldLoc, getQuadrant(), loc));
	}
	
}
