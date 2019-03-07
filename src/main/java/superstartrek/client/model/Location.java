package superstartrek.client.model;

public class Location {

	protected final int x;
	protected final int y;
	
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
	
	public Location(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Location(Location l) {
		this.x = l.getX();
		this.y = l.getY();
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
}
