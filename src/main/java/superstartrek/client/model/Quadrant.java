package superstartrek.client.model;

import java.util.ArrayList;
import java.util.List;

import superstartrek.client.Application;
import superstartrek.client.activities.klingons.Klingon;

public class Quadrant implements GeometricLookup{

	protected final String name;
	protected final int x;
	protected final int y;
	protected boolean explored;
	protected List<Star> stars = new ArrayList<>();
	protected StarBase starBase;
	protected List<Klingon> klingons = new ArrayList<>();
	
	public Quadrant(String name, int x, int y) {
		this.name = name;
		this.x = x;
		this.y = y;
	}
	
	public boolean contains(Klingon klingon) {
		return klingons.contains(klingon);
	}
	
	public void setStarBase(StarBase starBase) {
		this.starBase = starBase;
	}
	
	public boolean isExplored() {
		return explored;
	}

	public void setExplored(boolean explored) {
		this.explored = explored;
	}

	public List<Klingon> getKlingons() {
		return klingons;
	}
	
	public List<Star> getStars() {
		return stars;
	}
	
	public StarBase getStarBase() {
		return starBase;
	}

	public String getName() {
		return name;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public Thing findThingAt(int x, int y) {
		return findThingAt(Location.location(x, y));
	}

	@Override
	public Thing findThingAt(Location location) {
		for (Thing t:stars)
			if (t.getLocation() == location)
				return t;
		for (Thing t:klingons)
			if (t.getLocation() == location)
				return t;
		if (starBase!=null && starBase.getLocation() == location)
			return starBase;
		Enterprise enterprise = Application.get().starMap.enterprise;
		Quadrant eq = enterprise.getQuadrant();
		return (eq == this && enterprise.getLocation() == location)?enterprise:null;
	}

}
