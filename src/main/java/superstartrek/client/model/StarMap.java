package superstartrek.client.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Random;

import superstartrek.client.activities.klingons.Klingon;

public class StarMap {

	protected Quadrant[][] quadrants = new Quadrant[8][8];
	public Enterprise enterprise;
	protected long starDate = 0;

	public static double distance(int x1, int y1, int x2, int y2) {
		int dx = x1 - x2;
		int dy = y1 - y2;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public long getStarDate() {
		return starDate;
	}

	public void advanceStarDate(long value) {
		starDate += value;
	}

	public static double distance(Location l1, Location l2) {
		return distance(l1.x, l1.y, l2.x, l2.y);
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
		if (enterprise.getQuadrant() == q && enterprise.getX() == x && enterprise.getY() == y)
			return enterprise;
		for (Star star : q.getStars())
			if (star.getX() == x && star.getY() == y)
				return star;
		if (q.getStarBase() != null)
			if (q.getStarBase().getX() == x && q.getStarBase().getY() == y)
				return q.getStarBase();
		for (Klingon klingon : q.getKlingons())
			if (klingon.getX() == x && klingon.getY() == y)
				return klingon;
		return null;
	}

	public Location findFreeSpot(Quadrant q) {
		while (true) {
			int x = Random.nextInt(8);
			int y = Random.nextInt(8);
			Thing thing = findThingAt(q, x, y);
			if (thing == null)
				return new Location(x, y);
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

	public List<Thing> findObstaclesInLine(final Quadrant q, int xFrom, int yFrom, int xTo, int yTo) {
		List<Thing> found = new ArrayList<>();
		walkLine(xFrom, yFrom, xTo, yTo, new Walker() {

			@Override
			public boolean visit(int x, int y) {
				Thing thing = findThingAt(q, x, y);
				if (thing != null) {
					found.add(thing);
				}
				return true;
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
					return new Location(x, y);
			}
		return null;
	}

	public boolean hasKlingons() {
		for (int x = 0; x < quadrants.length; x++)
			for (int y = 0; y < quadrants[x].length; y++)
				if (!quadrants[x][y].getKlingons().isEmpty())
					return true;
		return false;
	}

}
