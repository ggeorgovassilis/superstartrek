package superstartrek.client.model;

/**
 * Makes looking up {@link Thing}s by x:y easier
 * @author george
 *
 */
public class QuadrantIndex implements GeometricLookup{

	Thing[][] things = new Thing[8][8];
	
	public QuadrantIndex(Quadrant quadrant, StarMap starMap) {
		for (Thing t:starMap.getEverythingIn(quadrant))
			things[t.getLocation().getX()][t.getLocation().getY()] = t;
	}
	
	@Override
	public Thing findThingAt(Location loc) {
		return findThingAt(loc.getX(),loc.getY());
	}

	@Override
	public Thing findThingAt(int x, int y) {
		return things[x][y];
	}
}
