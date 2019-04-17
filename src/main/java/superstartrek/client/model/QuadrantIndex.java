package superstartrek.client.model;

/**
 * Makes looking up {@link Thing}s by x:y easier
 * @author george
 *
 */
public class QuadrantIndex {

	Thing[][] things = new Thing[8][8];
	
	public QuadrantIndex(Quadrant quadrant, StarMap starMap) {
		for (Thing t:starMap.getEverythingIn(quadrant))
			things[t.getLocation().getX()][t.getLocation().getY()] = t;
	}
	
	public Thing getThingAt(Location loc) {
		return getThingAt(loc.getX(),loc.getY());
	}

	public Thing getThingAt(int x, int y) {
		return things[x][y];
	}
}