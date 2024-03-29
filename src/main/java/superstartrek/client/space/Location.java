package superstartrek.client.space;

public class Location {

	protected final static Location[][] cache = new Location[Constants.SECTORS_EDGE][Constants.SECTORS_EDGE];
	static {
		for (int x=0;x<Constants.SECTORS_EDGE;x++)
			for (int y=0;y<Constants.SECTORS_EDGE;y++)
				cache[x][y] = new Location(x,y);
	}

	public final int x;
	public final int y;

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

}
