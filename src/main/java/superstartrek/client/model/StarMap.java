package superstartrek.client.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Random;

public class StarMap {

	protected Quadrant[][] quadrants = new Quadrant[8][8];
	public Enterprise enterprise;

	public static double distance(int x1, int y1, int x2, int y2) {
		int dx = x1 - x2;
		int dy = y1 - y2;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public static double distance(Location l1, Location l2) {
		return distance(l1.x, l1.y, l2.x, l2.y);
	}

	public Quadrant getQuadrant(int x, int y) {
		if (x < 0 || x > 7)
			throw new IllegalArgumentException("x = " + x);
		if (y < 0 || y > 7)
			throw new IllegalArgumentException("y = " + y);
		return quadrants[x][y];
	}

	public Thing findThingAt(Quadrant q, int x, int y) {
		if (enterprise.getQuadrant() == q && enterprise.getX() == x && enterprise.getY() == y)
			return enterprise;
		for (Star star : q.getStars())
			if (star.getX() == x && star.getY() == y)
				return star;
		for (StarBase starBase : q.getStarBases())
			if (starBase.getX() == x && starBase.getY() == y)
				return starBase;
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

	public void walkLine(Quadrant q, int x0, int y0, int x1, int y1, Walker callback) {
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
			if (!callback.visit(q, x0, y0))
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

	public List<Thing> findObstaclesInLine(Quadrant q, int xFrom, int yFrom, int xTo, int yTo) {
		List<Thing> found = new ArrayList<>();
		walkLine(q, xFrom, yFrom, xTo, yTo, new Walker() {

			@Override
			public boolean visit(Quadrant q, int x, int y) {
				Thing thing = findThingAt(q, x, y);
				if (thing != null) {
					found.add(thing);
				}
				return true;
			}
		});
		return found;
	}

}
