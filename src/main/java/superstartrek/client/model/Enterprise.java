package superstartrek.client.model;

import superstartrek.client.Application;
import superstartrek.client.activities.navigation.ThingMovedEvent;

public class Enterprise extends Thing{

	protected Application application;
	
	public Enterprise(Application app) {
		this.application = app;
		setName("NCC 1701 USS Enterprise");
		setSymbol("O=Ξ");
		setCss("enterprise");
	}
	
	public void navigateTo(Location loc) {
		Location oldLoc = new Location(getX(), getY());
		this.setLocation(loc);
		application.events.fireEvent(new ThingMovedEvent(this, getQuadrant(), oldLoc, getQuadrant(), loc));
	}
	
}
