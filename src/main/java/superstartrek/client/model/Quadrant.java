package superstartrek.client.model;

import java.util.ArrayList;
import java.util.List;

public class Quadrant {

	protected final String name;
	protected final int x;
	protected final int y;
	protected List<Star> stars = new ArrayList<>();
	
	public Thing thing(int x, int y) {
		for (Star star:stars)
			if (star.getX() == x && star.getY() == y)
				return star;
		return null;
	}

	public Quadrant(String name, int x, int y) {
		this.name = name;
		this.x = x;
		this.y = y;
	}
	
	public List<Star> getStars() {
		return stars;
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
