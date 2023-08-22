package superstartrek.client.space;

import java.util.ArrayList;
import java.util.List;
import superstartrek.client.Application;
import superstartrek.client.utils.BrowserAPI;
import superstartrek.client.vessels.Enterprise;

public class StarMap {

	protected Quadrant[][] quadrants = new Quadrant[Constants.SECTORS_EDGE][Constants.SECTORS_EDGE];
	public Enterprise enterprise;
	protected int starDate = 2100;

	public static boolean within_distance(int x1, int y1, int x2, int y2, double range) {
		int dx = x1 - x2;
		int dy = y1 - y2;
		return dx * dx + dy * dy <= range * range;
	}

	public static double distance(int x1, int y1, int x2, int y2) {
		int dx = x1 - x2;
		int dy = y1 - y2;
		return Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * It is faster to compute squares then square roots
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static int distance_squared(int x1, int y1, int x2, int y2) {
		int dx = x1 - x2;
		int dy = y1 - y2;
		return dx * dx + dy * dy;
	}

	public void markAsExploredAround(Quadrant q) {
		int xFrom = Math.max(0, q.x - 1);
		int xTo = Math.min(Constants.SECTORS_EDGE-1, q.x + 1);
		int yFrom = Math.max(0, q.y - 1);
		int yTo = Math.min(Constants.SECTORS_EDGE-1, q.y + 1);
		for (int y = yFrom; y <= yTo; y++)
			for (int x = xFrom; x <= xTo; x++)
				getQuadrant(x, y).setExplored(true);
	}

	public static double distance(Thing a, Thing b) {
		return distance(a.getLocation(), b.getLocation());
	}

	public static double distance(Location la, Location lb) {
		return distance(la.x, la.y, lb.x, lb.y);
	}

	public void setQuadrant(Quadrant q) {
		quadrants[q.x][q.y] = q;
	}

	public int getStarDate() {
		return starDate;
	}
	
	public void setStarDate(int sd) {
		this.starDate = sd;
	}

	public void advanceStarDate(int value) {
		starDate += value;
	}

	public static boolean within_distance(Location l1, Location l2, double range) {
		return within_distance(l1.x, l1.y, l2.x, l2.y, range);
	}

	public static boolean within_distance(Thing t1, Thing t2, double range) {
		return within_distance(t1.getLocation(), t2.getLocation(), range);
	}

	public boolean isOnMap(int x, int y) {
		return (x >= 0 && x <= Constants.SECTORS_EDGE-1 && y >= 0 && y <= Constants.SECTORS_EDGE-1);
	}

	public Quadrant getQuadrant(int x, int y) {
		if (!isOnMap(x, y))
			throw new IllegalArgumentException(x + ":" + y);
		return quadrants[x][y];
	}

	public Location findFreeSpot(Quadrant q) {
		BrowserAPI random = Application.get().browserAPI;
		//This should always terminate; quadrants are rather sparsely populated
		while (true) {
			int x = random.nextInt(Constants.SECTORS_EDGE);
			int y = random.nextInt(Constants.SECTORS_EDGE);
			Thing thing = q.findThingAt(x, y);
			if (thing == null)
				return Location.location(x, y);
		}
	}

	public static void walkLine(int x0, int y0, int x1, int y1, Walker callback) {
		int sx = 0;
		int sy = 0;
		int err = 0;
		int e2 = 0;
		int dx = Math.abs(x1 - x0);
		int dy = Math.abs(y1 - y0);
		if (x0 < x1)
			sx = 1;
		else
			sx = -1;
		if (y0 < y1)
			sy = 1;
		else
			sy = -1;
		err = dx - dy;

		while (true) {
			if (!callback.visit(x0, y0))
				break;
			if (x0 == x1 && y0 == y1)
				break;
			e2 = 2 * err;
			if (e2 > -dy) {
				err = err - dy;
				x0 = x0 + sx;
			}
			if (e2 < dx) {
				err = err + dx;
				y0 = y0 + sy;
			}
		}
	};

	/**
	 * Finds all obstacles in the line connecting from (inclusive) to to
	 * (inclusive). If from and to are occupied by {@link Thing}s, then those will
	 * be the only objects returned.
	 * 
	 * @param q
	 * @param from
	 * @param to
	 * @param cap  stop after finding that many obstacles
	 * @return
	 */
	public static List<Thing> findObstaclesInLine(Quadrant q, Location from, Location to, int cap) {
		List<Thing> found = new ArrayList<>();
		walkLine(from.x, from.y, to.x, to.y, (x, y) -> {
			Thing thing = q.findThingAt(x, y);
			if (thing != null) {
				found.add(thing);
			}
			return found.size() < cap;
		});
		return found;
	}

	public Location findFreeSpotAround(Quadrant index, Location loc, int maxRadius) {
		BrowserAPI random = Application.get().browserAPI;
		for (int radius = 1; radius <= maxRadius; radius++) {
			//While this may seem unnecessarily heuristic and wasteful, it is not:
			//1. quadrants are sparsely populated, the chance of finding a free spot is high
			//2. game play relies on randomness, eg. retreating Klingons would become predictable
			for (int remainingTries = radius*radius ; remainingTries > 0; remainingTries--) {
				int x = Math.min(Constants.SECTORS_EDGE-1, Math.max(0, loc.x + (random.nextInt(1 + radius)) - (radius / 2)));
				int y = Math.min(Constants.SECTORS_EDGE-1, Math.max(0, loc.y + (random.nextInt(1 + radius)) - (radius / 2)));
				if (null == index.findThingAt(x, y))
					return Location.location(x, y);
			}
		}
		return null;
	}

	public boolean hasKlingons() {
		for (int x = 0; x < quadrants.length; x++)
			for (int y = 0; y < quadrants[x].length; y++)
				// quadrant could be null in unit test
				if (quadrants[x][y] != null && quadrants[x][y].hasKlingons())
					return true;
		return false;
	}

}
