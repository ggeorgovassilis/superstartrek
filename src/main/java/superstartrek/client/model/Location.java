package superstartrek.client.model;

public class Location {

	protected int x;
	protected int y;
	
	@Override
	public int hashCode() {
		return x+y*10;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Location) && ((Location)obj).getX() == x && ((Location)obj).getY()==y;
	}
	
	@Override
	public String toString() {
		return x+":"+y;
	}
	
	public Location() {
	}

	public Location(int x, int y) {
		setX(x);
		setY(y);
	}
	
	public Location(Location l) {
		setX(l.getX());
		setY(l.getY());
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
