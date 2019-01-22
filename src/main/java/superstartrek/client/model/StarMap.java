package superstartrek.client.model;

public class StarMap {

	protected Quadrant[][] quadrants = new Quadrant[8][8];
	
	public Quadrant getQuadrant(int x, int y) {
		if (x<0||x>7) throw new IllegalArgumentException("x = "+x);
		if (y<0||y>7) throw new IllegalArgumentException("y = "+y);
		return quadrants[x][y];
	}
	
}
