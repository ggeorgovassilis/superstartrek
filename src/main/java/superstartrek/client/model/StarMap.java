package superstartrek.client.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import superstartrek.client.Application;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.utils.Random;

public class StarMap {

	protected Quadrant[][] quadrants = new Quadrant[8][8];
	public Enterprise enterprise;
	protected long starDate = 0;

	public static boolean within_distance(int x1, int y1, int x2, int y2, double range) {
		int dx = x1 - x2;
		int dy = y1 - y2;
		return dx * dx + dy * dy < range*range;
	}

	
	public static double distance(int x1, int y1, int x2, int y2) {
		int dx = x1 - x2;
		int dy = y1 - y2;
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	public static int distance_squared(int x1, int y1, int x2, int y2) {
		int dx = x1 - x2;
		int dy = y1 - y2;
		return dx*dx +dy*dy;
	}

	public static double distance(Thing a, Thing b) {
		return distance(a.getLocation(), b.getLocation());
	}

	public static double distance(Location la, Location lb) {
		return distance(la.getX(), la.getY(), lb.getX(), lb.getY());
	}

	public static Set<Location> locations(List<? extends Thing> things) {
		Set<Location> locations = new HashSet<>();
		for (Thing t : things)
			locations.add(t.getLocation());
		return locations;
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

	public static boolean within_distance(Thing t1, Location l2, double range) {
		return within_distance(t1.getLocation(), l2, range);
	}
	
	public boolean isOnMap(int x, int y) {
		return (x >= 0 && x <= 7 && y >= 0 && y <= 7);
	}

	public Quadrant getQuadrant(int x, int y) {
		if (!isOnMap(x, y))
			throw new IllegalArgumentException(x + ":" + y);
		return quadrants[x][y];
	}

	public Thing findThingAt(Quadrant q, int x, int y) {
		Location loc = Location.location(x, y);
		if (enterprise.getQuadrant() == q && enterprise.getLocation() == loc)
			return enterprise;
		for (Star star : q.getStars())
			if (star.getLocation() == loc)
				return star;
		if (q.getStarBase() != null)
			if (q.getStarBase().getLocation() == loc)
				return q.getStarBase();
		for (Klingon klingon : q.getKlingons())
			if (klingon.getLocation() == loc)
				return klingon;
		return null;
	}

	public Location findFreeSpot(Quadrant q) {
		Random random = Application.get().random;
		while (true) {
			int x = random.nextInt(8);
			int y = random.nextInt(8);
			Thing thing = findThingAt(q, x, y);
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
	 * Finds the all obstacles in the line connecting from (inclusive) to to (inclusive).
	 * If from and to are occupied by {@link Thing}s, then those will be the only objects returned.
	 * @param q
	 * @param from
	 * @param to
	 * @param cap stop after finding that many obstacles
	 * @return
	 */
	public List<Thing> findObstaclesInLine(Quadrant q, Location from, Location to, int cap) {
		List<Thing> found = new ArrayList<>();
		walkLine(from.getX(), from.getY(), to.getX(), to.getY(), new Walker() {

			@Override
			public boolean visit(int x, int y) {
				Thing thing = findThingAt(q, x, y);
				if (thing != null) {
					found.add(thing);
				}
				return found.size()<2;
			}
		});
		return found;
	}

	public Location findFreeSpotAround(Quadrant q, Location loc, int radius) {
		int minX = Math.max(0, loc.getX() - 1);
		int minY = Math.max(0, loc.getY() - 1);
		int maxX = Math.min(7, loc.getX() + 1);
		int maxY = Math.min(7, loc.getY() + 1);
		for (int x = minX; x <= maxX; x++)
			for (int y = minY; y <= maxY; y++) {
				if (null == findThingAt(q, x, y))
					return Location.location(x, y);
			}
		return null;
	}

	public boolean hasKlingons() {
		for (int x = 0; x < quadrants.length; x++)
			for (int y = 0; y < quadrants[x].length; y++)
				if (quadrants[x][y]!=null && !quadrants[x][y].getKlingons().isEmpty())
					return true;
		return false;
	}
	
	public List<Thing> getEverythingIn(Quadrant quadrant){
		List<Thing> things = new ArrayList<>(quadrant.getStars());
		things.addAll(quadrant.getKlingons());
		if (quadrant.starBase!=null)
			things.add(quadrant.starBase);
		if (enterprise.getQuadrant() == quadrant)
			things.add(enterprise);
		return things;
	}

}
