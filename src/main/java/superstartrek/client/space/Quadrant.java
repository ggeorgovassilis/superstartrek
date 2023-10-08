package superstartrek.client.space;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.google.gwt.core.client.GWT;

import superstartrek.client.Application;
import superstartrek.client.vessels.Enterprise;
import superstartrek.client.vessels.Klingon;

public class Quadrant{

	protected final String name;
	public final int x;
	public final int y;
	protected boolean explored;
	private List<Star> stars = new ArrayList<>();
	private StarBase starBase;
	private List<Klingon> klingons = new ArrayList<>();
	private Thing[][] things;
	
	public Quadrant(String name, int x, int y) {
		this.name = name;
		this.x = x;
		this.y = y;
	}
	
	public void dehydrate() {
		GWT.log("Dehydrating "+x+","+y);
		things = null;
	}
	
	public void hydrate() {
		if (things !=null)
			return;
		GWT.log("Hydrating "+x+","+y);
		things = new Thing[Constants.SECTORS_EDGE][Constants.SECTORS_EDGE];
		doWithThings(t->things[t.location.x][t.location.y]=t);
	}

	public boolean contains(Klingon klingon) {
		return klingons.contains(klingon);
	}
	
	public void setStarBase(StarBase starBase) {
		this.starBase = starBase;
		mark(starBase);
	}

	public boolean isExplored() {
		return explored;
	}

	public void setExplored(boolean explored) {
		this.explored = explored;
	}

	public boolean hasKlingons() {
		return !klingons.isEmpty();
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

	public Thing findThingAt(int x, int y) {
		hydrate();
		return things[x][y];
	}

	public Thing findThingAt(Location location) {
		return findThingAt(location.x, location.y);
	}
	
	private void mark(Thing thing) {
		hydrate();
		things[thing.getLocation().x][thing.getLocation().y] = thing;
	}
	
	private void clear(Location location) {
		if (things!=null)
			things[location.x][location.y] = null;
	}
	
	public void add(Klingon klingon) {
		klingons.add(klingon);
		mark(klingon);
	}

	public void add(Star star) {
		stars.add(star);
		mark(star);
	}
	
	public void remove(Klingon klingon) {
		klingons.remove(klingon);
		clear(klingon.getLocation());
	}
	
	public void add(Enterprise e) {
		mark(e);
	}
	
	public void remove(Enterprise e) {
		clear(e.getLocation());
	}
	
	public void doWithThings(Consumer<Thing> consumer) {
		for (Thing thing:stars)
			consumer.accept(thing);
		for (Thing thing:klingons)
			consumer.accept(thing);
		if (starBase!=null)
			consumer.accept(starBase);
		Enterprise enterprise = Application.get().starMap.enterprise;
		//enterprise can be null during map de-serialization
		if (enterprise!=null && enterprise.getQuadrant() == this)
			consumer.accept(enterprise);
		
	};
	
}
