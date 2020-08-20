package superstartrek.client.model;

import java.util.ArrayList;
import java.util.List;
import superstartrek.client.Application;
import superstartrek.client.utils.BrowserAPI;

public class StarMap {

	protected Quadrant[][] quadrants = new Quadrant[Constants.SECTORS_EDGE][Constants.SECTORS_EDGE];
	public Enterprise enterprise;
	//TODO: this should probably be an int
	protected long starDate = 2100;

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

	public static int distance_squared(int x1, int y1, int x2, int y2) {
		int dx = x1 - x2;
		int dy = y1 - y2;
		return dx * dx + dy * dy;
	}

	public void markAsExploredAround(Quadrant q) {
		int xFrom = Math.max(0, q.getX() - 1);
		int xTo = Math.min(Constants.SECTORS_EDGE-1, q.getX() + 1);
		int yFrom = Math.max(0, q.getY() - 1);
		int yTo = Math.min(Constants.SECTORS_EDGE-1, q.getY() + 1);
		for (int y = yFrom; y <= yTo; y++)
			for (int x = xFrom; x <= xTo; x++)
				getQuadrant(x, y).setExplored(true);
	}

	public static double distance(Thing a, Thing b) {
		return distance(a.getLocation(), b.getLocation());
	}

	public static double distance(Location la, Location lb) {
		return distance(la.getX(), la.getY(), lb.getX(), lb.getY());
	}

	public void setQuadrant(Quadrant q) {
		quadrants[q.getX()][q.getY()] = q;
	}

	public long getStarDate() {
		return starDate;
	}
	
	public void setStarDate(long sd) {
		this.starDate = sd;
	}

	public void advanceStarDate(long value) {
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
		QuadrantIndex index = new QuadrantIndex(q, this);
		//This should always terminate; quadrats are rather sparesely populated
		while (true) {
			int x = random.nextInt(Constants.SECTORS_EDGE);
			int y = random.nextInt(Constants.SECTORS_EDGE);
			Thing thing = index.findThingAt(x, y);
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
	 * Finds the all obstacles in the line connecting from (inclusive) to to
	 * (inclusive). If from and to are occupied by {@link Thing}s, then those will
	 * be the only objects returned.
	 * 
	 * @param q
	 * @param from
	 * @param to
	 * @param cap  stop after finding that many obstacles
	 * @return
	 */
	public static List<Thing> findObstaclesInLine(QuadrantIndex q, Location from, Location to, int cap) {
		List<Thing> found = new ArrayList<>();
		walkLine(from.getX(), from.getY(), to.getX(), to.getY(), (x, y) -> {
			Thing thing = q.findThingAt(x, y);
			if (thing != null) {
				found.add(thing);
			}
			return found.size() < cap;
		});
		return found;
	}

	public Location findFreeSpotAround(QuadrantIndex index, Location loc, int maxRadius) {
		BrowserAPI random = Application.get().browserAPI;
		for (int radius = 1; radius <= maxRadius; radius++) {
			//TODO seems unnecessarily heuristic and wasteful. find a better, more deterministic way
			for (int tries = 0; tries < radius * radius; tries++) {
				int x = Math.min(Constants.SECTORS_EDGE-1, Math.max(0, loc.getX() + (random.nextInt(1 + radius)) - (radius / 2)));
				int y = Math.min(Constants.SECTORS_EDGE-1, Math.max(0, loc.getY() + (random.nextInt(1 + radius)) - (radius / 2)));
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
				if (quadrants[x][y] != null && !quadrants[x][y].getKlingons().isEmpty())
					return true;
		return false;
	}

	public List<Thing> getEverythingIn(Quadrant quadrant) {
		List<Thing> things = new ArrayList<>(quadrant.getStars());
		things.addAll(quadrant.getKlingons());
		if (quadrant.starBase != null)
			things.add(quadrant.starBase);
		if (enterprise.getQuadrant() == quadrant)
			things.add(enterprise);
		return things;
	}

}
