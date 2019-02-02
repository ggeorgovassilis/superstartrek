package superstartrek.client.model;

import java.util.List;

import superstartrek.client.Application;
import superstartrek.client.activities.combat.FireEvent;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.navigation.EnterpriseWarpedEvent;
import superstartrek.client.activities.navigation.ThingMovedEvent;

public class Enterprise extends Vessel{
	
	protected Setting phasers = new Setting(50, 50);

	public Enterprise(Application app) {
		super(app, new Setting(3,3), new Setting(100,100));
		setName("NCC 1701 USS Enterprise");
		setSymbol("O=Ξ");
		setCss("enterprise");
	}
	
	public void warpTo(Quadrant qTo) {
		EnterpriseWarpedEvent event = new EnterpriseWarpedEvent(this, getQuadrant(), 
				new Location(getX(), getY()), qTo, new Location(getX(), getY()));
		setQuadrant(qTo);
		int qx = qTo.getX();
		int qy = qTo.getY();
		StarMap map = application.starMap;
		int xFrom = Math.max(0, qx-1);
		int xTo = Math.min(7, qx+1);
		int yFrom = Math.max(0, qy-1);
		int yTo = Math.min(7, qy+1);
		for (int y=yFrom;y<=yTo;y++)
		for (int x=xFrom;x<=xTo;x++)
			map.getQuadrant(x, y).setExplored(true);
		application.events.fireEvent(event);
	}
	
	// only for internal use, bypasses checks
	public void _navigateTo(Location loc) {
		Location oldLoc = new Location(getX(), getY());
		this.setLocation(loc);
		application.events.fireEvent(new ThingMovedEvent(this, getQuadrant(), oldLoc, getQuadrant(), loc));
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
			application.message("Path isn't clear "+things.size()+" "+things.get(1).getName()+" "+things.get(1));
			return;
		}
		_navigateTo(loc);
	}
	
	public void firePhasersAt(Location sector) {
		Thing thing = application.starMap.findThingAt(quadrant, sector.getX(), sector.getY());
		if (thing == null) {
			application.message("There is nothing at "+sector);
			return;
		}
		if (!(thing instanceof Klingon)) {
			application.message("Phasers can target only enemy vessels");
			return;
		}
		double distance = StarMap.distance(this, thing);
		if (distance>3) {
			application.message("Target is too far away.");
			return;
		}
		Klingon klingon = (Klingon)thing;
		FireEvent event = new FireEvent(this, klingon, "phasers", phasers.getValue()/distance);
		application.events.fireEvent(event);
	}
	
	public void dockAtStarbase(StarBase starBase) {
		phasers.repair();
	}
}
