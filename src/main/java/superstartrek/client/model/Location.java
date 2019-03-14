package superstartrek.client.model;

public class Location {

	protected final static Location[][] cache = new Location[8][8];
	static {
		for (int x=0;x<8;x++)
			for (int y=0;y<8;y++)
				cache[x][y] = new Location(x,y);
	}

	protected final int x;
	protected final int y;

	@Override
	public String toString() {
		return x+":"+y;
	}
	
	private Location(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public static Location location(int x, int y) {
		return cache[x][y];
	}

	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
}
