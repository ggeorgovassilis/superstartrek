package superstartrek.client.model;

import com.google.gwt.user.client.Random;

public class StarMap {

	protected Quadrant[][] quadrants = new Quadrant[8][8];
	public Enterprise enterprise;
	
	
	public Quadrant getQuadrant(int x, int y) {
		if (x<0||x>7) throw new IllegalArgumentException("x = "+x);
		if (y<0||y>7) throw new IllegalArgumentException("y = "+y);
		return quadrants[x][y];
	}
	
	
	public Thing findThingAt(Quadrant q, int x, int y) {
		if (enterprise.getQuadrant() == q && enterprise.getX() == x && enterprise.getY() == y)
			return enterprise;
		for (Star star:q.getStars())
			if (star.getX() == x && star.getY() == y)
				return star;
		for (StarBase starBase:q.getStarBases())
			if (starBase.getX() == x && starBase.getY() == y)
				return starBase;
		for (Klingon klingon:q.getKlingons())
			if (klingon.getX() == x && klingon.getY() == y)
				return klingon;
		return null;
	}
	
	public Location findFreeSpot(Quadrant q) {
		while(true) {
			int x = Random.nextInt(8);
			int y = Random.nextInt(8);
			Thing thing = findThingAt(q, x, y);
			if (thing == null)
				return new Location(x,y);
		}
	}
	
}
