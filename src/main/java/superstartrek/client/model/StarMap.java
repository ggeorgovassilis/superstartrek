package superstartrek.client.model;

import java.util.ArrayList;
import java.util.List;
import superstartrek.client.Application;
import superstartrek.client.utils.Random;

public class StarMap {

	protected Quadrant[][] quadrants = new Quadrant[8][8];
	public Enterprise enterprise;
	protected long starDate = 0;

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
		int xTo = Math.min(7, q.getX() + 1);
		int yFrom = Math.max(0, q.getY() - 1);
		int yTo = Math.min(7, q.getY() + 1);
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
		return (x >= 0 && x <= 7 && y >= 0 && y <= 7);
	}

	public Quadrant getQuadrant(int x, int y) {
		if (!isOnMap(x, y))
			throw new IllegalArgumentException(x + ":" + y);
		return quadrants[x][y];
	}

	public Location findFreeSpot(Quadrant q) {
		Random random = Application.get().random;
		QuadrantIndex index = new QuadrantIndex(q, this);
		while (true) {
			int x = random.nextInt(8);
			int y = random.nextInt(8);
			Thing thing = index.findThingAt(x, y);
			if (thing == null)
				return Location.location(x, y);
		}
	}

	public void walkLine(int x0, int y0, int x1, int y1, Walker callback) {
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
	public List<Thing> findObstaclesInLine(QuadrantIndex q, Location from, Location to, int cap) {
		List<Thing> found = new ArrayList<>();
		walkLine(from.getX(), from.getY(), to.getX(), to.getY(), new Walker() {

			@Override
			public boolean visit(int x, int y) {
				Thing thing = q.findThingAt(x, y);
				if (thing != null) {
					found.add(thing);
				}
				return found.size() < cap;
			}
		});
		return found;
	}

	public List<Thing> findObstaclesInLine(Quadrant q, Location from, Location to, int cap) {
		QuadrantIndex index = new QuadrantIndex(q, this);
		return findObstaclesInLine(index, from, to, cap);
	}

	public Location findFreeSpotAround(Quadrant q, Location loc) {
		return findFreeSpotAround(q, loc, 8);
	}

	public Location findFreeSpotAround(Quadrant q, Location loc, int maxRadius) {
		QuadrantIndex index = new QuadrantIndex(q, this);
		Random random = Application.get().random;
		for (int radius = 1; radius <= maxRadius; radius++) {
			for (int tries = 0; tries < radius * radius; tries++) {
				int x = Math.min(7, Math.max(0, loc.getX() + (random.nextInt(1+radius))-(radius/2)));
				int y = Math.min(7, Math.max(0, loc.getY() + (random.nextInt(1+radius))-(radius/2)));
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
