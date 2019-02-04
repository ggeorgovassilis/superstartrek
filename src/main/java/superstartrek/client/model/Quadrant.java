package superstartrek.client.model;

import java.util.ArrayList;
import java.util.List;

import superstartrek.client.activities.klingons.Klingon;

public class Quadrant {

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

}