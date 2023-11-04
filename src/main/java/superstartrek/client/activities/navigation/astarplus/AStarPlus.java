package superstartrek.client.activities.navigation.astarplus;

import java.util.ArrayList;
import java.util.List;

import superstartrek.client.space.Constants;
import superstartrek.client.space.Location;
import superstartrek.client.space.Quadrant;
import superstartrek.client.space.Thing;

/**
 * Short explanation of the implementation: normal a* is based on a graph which
 * models locations as vertices and connections as edges. The SST map is a 8x8
 * 2D specialisation where vertices are connected only to their NESW neighbors.
 * We use a trick to improve performance: each square can be addressed by a
 * positive integer (y*8+x+1). A linear "matrix" array notes down whether a
 * square is occupied (-1), has not been visited yet (0) or has been visited
 * (>0), in which case it contains the ID of the square it was visited from. The
 * todo list is also a linear array with a sliding head and tail; as "todo" is
 * guaranteed to be smaller than 64 entries, we don't need complex memory
 * management and can use a fixed size array for that.
 * 
 * The algorithm then walks back the matrix from the destination to the start, keeping
 * the path in a list. Normally the list must be reversed (since it codes the path
 * from end to start), but by actually switching the initial arguments start <-> end
 * we can compute the path from end to start and skip the list reversal.
 * 
 * Note about performance: profiling shows that this implementation is fast, 
 * self-time spent isn't even in the top 20.
 *
 */
public class AStarPlus {

	final static int OCCUPIED = -1;
	final static int FREE = 0;
	final int[] matrix = new int[1 + (Constants.SECTORS_EDGE * Constants.SECTORS_EDGE)]; // 0 is never used, look into
																							// code for explanation
	final int[] todo = new int[Constants.SECTORS_EDGE * Constants.SECTORS_EDGE];
	int todoHead = 0;
	int todoTail = 0;

	int coordsToIndex(int x, int y) {
		// need to add 1 because "0" (0*8+0) means "free"
		return 1 + x + y * Constants.SECTORS_EDGE;
	}

	int indexToX(int index) {
		return (index - 1) % Constants.SECTORS_EDGE;
	}

	int indexToY(int index) {
		return (index - 1) / Constants.SECTORS_EDGE;
	}

	void addToDo(int index) {
		todo[todoTail++] = index;
	}

	int getNextTodo() {
		// it's better to do the overflow check here rather than in addTodo because:
		// 1. addToDo can't break a head<tail relation unless it's broken already, so a
		// check there would
		// only uncover an already broken association
		// 2. getNextTodo is called _at_most_ as many times as addToDo, usually fewer
		// times
		assert(todoHead<todoTail);
		return todo[todoHead++];
	}

	boolean hasMoreTodo() {
		return todoTail > todoHead;
	}

	/**
	 * 0 means traversable and not visited yet -1 means not traversable anything
	 * else means visited where the value is the index of the sector we came from
	 */

	void markOccupied(Thing thing) {
		Location l = thing.getLocation();
		int index = coordsToIndex(l.x, l.y);
		matrix[index] = OCCUPIED;
	}

	void initialiseMatrix(Quadrant quadrant) {
		for (int i = 0; i < matrix.length; i++)
			matrix[i] = FREE;
		quadrant.doWithThings(obstacle -> markOccupied(obstacle));
	}

	void addNeighboursToTodo(int fromSectorIndex) {
		int sectorX = indexToX(fromSectorIndex);
		int sectorY = indexToY(fromSectorIndex);

		int minX = Math.max(0, sectorX - 1);
		int maxX = Math.min(Constants.SECTORS_EDGE - 1, sectorX + 1);
		int minY = Math.max(0, sectorY - 1);
		int maxY = Math.min(Constants.SECTORS_EDGE - 1, sectorY + 1);
		int dx = maxX - minX;
		int index = coordsToIndex(minX, minY);
		for (int y = minY; y <= maxY; y++) {
			final int rowStartIndex = index;
			final int rowEndIndex = index + dx;
			for (; index <= rowEndIndex; index++) {
				if (index != fromSectorIndex && matrix[index] == FREE) {
					matrix[index] = fromSectorIndex;
					addToDo(index);
				}
			}
			index = rowStartIndex + Constants.SECTORS_EDGE;
		}
	}

	//TODO: idea for improvement
	//The only use case for AStarPlus is Klingons moving towards the Enterprise.
	//As they move only a few sectors per turn, we don't need the entire path - just the first one or two steps.
	List<Location> reconstructPath(int indexFrom, int indexTo, int trimSteps) {
		List<Location> path = new ArrayList<Location>();
		int previousIndex = indexTo;
		do {
			int x = indexToX(previousIndex);
			int y = indexToY(previousIndex);
			if (indexTo!=previousIndex)
				path.add(Location.location(x, y));
			previousIndex = matrix[previousIndex];
			trimSteps--;
		} while (previousIndex != indexFrom && trimSteps>=0);

		// Collections.reverse(path);
		return path;
	}

	/**
	 * Finds path between "from" and "to".
	 * @param from Start path here. The returned list does not contain "from"
	 * @param to End path here. The returned list contains "to".
	 * @param quadrant Every "Thing" in this Quadrant is considered an obstacle (excluding anything at "to").
	 * @param trimSteps Return only that many steps in the path. This is a performance
	 * optimisation. The entire path is still computed, but if only the first "trimSteps" number of steps are needed,
	 * the algorithm can run a bit faster.
	 * @return
	 */
	public List<Location> findPathBetween(Location from, Location to, Quadrant quadrant, int trimSteps) {
		if (from == to)
			return new ArrayList<Location>();
		// the implementation works in reverse: it traces steps from "to" towards "from".
		// this would require reversing the list at the end; by swapping terminals
		// we skip that step.
		Location tmp = from;
		from = to;
		to = tmp;
		initialiseMatrix(quadrant);
		final int indexFrom = coordsToIndex(from.x, from.y);
		addToDo(indexFrom);
		matrix[indexFrom] = OCCUPIED;
		int indexTo = coordsToIndex(to.x, to.y);
		matrix[indexTo] = FREE;
		while (hasMoreTodo()) {
			int indexNext = getNextTodo();
			if (indexNext == indexTo) {
				return reconstructPath(indexFrom, indexTo, trimSteps);
			}
			addNeighboursToTodo(indexNext);
		}
		return new ArrayList<Location>();
	}
}
