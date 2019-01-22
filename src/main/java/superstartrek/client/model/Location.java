package superstartrek.client.model;

public class Location {

	protected int x;
	protected int y;
	
	public Location() {
	}

	public Location(int x, int y) {
		setX(x);
		setY(y);
	}

	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
}
