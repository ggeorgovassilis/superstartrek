package superstartrek.client.model;

import java.util.ArrayList;
import java.util.List;

public class Quadrant {

	protected final String name;
	protected final int x;
	protected final int y;
	protected List<Star> stars = new ArrayList<>();
	protected List<StarBase> starBases = new ArrayList<>();
	protected List<Klingon> klingons = new ArrayList<>();
	
	public Quadrant(String name, int x, int y) {
		this.name = name;
		this.x = x;
		this.y = y;
	}
	
	public List<Klingon> getKlingons() {
		return klingons;
	}
	
	public List<Star> getStars() {
		return stars;
	}
	
	public List<StarBase> getStarBases() {
		return starBases;
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
