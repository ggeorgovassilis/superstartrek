package superstartrek.client.activities.navigation.astarplus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;

/**
 * Short explanation of the implementation: normal a* is based on a graph which models locations as vertices and connections as edges.
 * The SST map is a 8x8 2D specialisation where vertices are connected only to their NESW neighbors. We use a trick to improve performance:
 * each square can be addressed by a positive integer (y*8+x+1). A linear "matrix" array notes down whether a square is occupied (-1), has not
 * been visited yet (0) or has been visited (>0), in which case it contains the ID of the square it was visited from.
 * The todo list is also a linear array with a sliding head and tail; as "todo" is guaranteed to be smaller than 64 entries, we don't need complex
 * memory management and can use a fixed size array for that.
 * 
 * 
 *
 */
public class AStarPlus {

	final static int OCCUPIED = -1;
	final static int FREE = 0;
	final int[] matrix = new int[65]; // 0 is never used, look into code for explanation
	final int[] todo = new int[64];
	int todoHead = 0;
	int todoTail = 0;

	int coordsToIndex(int x, int y) {
		// need to add 1 because "0" (0*8+8) means "free"
		return 1 + x + y * 8;
	}

	int indexToX(int index) {
		return (index - 1) % 8;
	}

	int indexToY(int index) {
		return (index - 1) / 8;
	}

	void addToDo(int index) {
		todo[todoTail++] = index;
	}

	int getNextTodo() {
		//it's better to do the overflow check here rather than in addTodo because:
		//1. addToDo can't break a head<tail relation unless it's broken already, so a check there would
		//only uncover an already broken association
		//2. getNextTodo is called _at_most_ as many times as addToDo, usually fewer times
		if (todoHead >= todoTail)
			throw new IndexOutOfBoundsException("Head after tail " + todoHead + " " + todoTail);
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
		int index = coordsToIndex(l.getX(), l.getY());
		matrix[index] = OCCUPIED;
	}

	void initialiseMatrix(Quadrant quadrant, StarMap map) {
		for (int i = 0; i < matrix.length; i++)
			matrix[i] = FREE;
		List<Thing> things = map.getEverythingIn(quadrant);
		for (Thing obstacle : things)
			markOccupied(obstacle);
	}

	void addNeighboursToTodo(int fromSectorIndex) {
		int sectorX = indexToX(fromSectorIndex);
		int sectorY = indexToY(fromSectorIndex);

		int minX = Math.max(0, sectorX - 1);
		int maxX = Math.min(7, sectorX + 1);
		int minY = Math.max(0, sectorY - 1);
		int maxY = Math.min(7, sectorY + 1);
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
			index = rowStartIndex + 8;
		}
	}

	List<Location> reconstructPath(int indexFrom, int indexTo) {
		List<Location> path = new ArrayList<Location>();
		int previousIndex = indexTo;
		while (previousIndex != indexFrom) {
			int x = indexToX(previousIndex);
			int y = indexToY(previousIndex);
			path.add(Location.location(x, y));
			previousIndex = matrix[previousIndex];
		}
		Collections.reverse(path);
		return path;
	}

	void printMatrix() {
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				int index = coordsToIndex(x, y);
				if (matrix[index] == FREE)
					System.out.print(" ");
				else if (matrix[index] == OCCUPIED)
					System.out.print("#");
				else
					System.out.print(".");
			}
			System.out.println();
		}
	}

	//TODO: idea how to speed up path construction a bit:
	//we actually never need the full path; all we need is:
	//1. knowing whether there is a path or not
	//2. the first 2 sectors of the path (because klingons can travel only that far in a turn)
	//The path is reversible: path(a,b) = path(b,a). The "reconstructPath" method jumps from "b" to "a", constructing the list of steps to walk from "a" to "b".
	//If we asked astarplus to construct the path from "b" to "a", the "reconstructPath" method would start reconstructing the path from "a" to "b"; so we could
	//abort the path reconstruction once the first two steps are found.
	public List<Location> findPathBetween(Location from, Location to, Quadrant quadrant, StarMap map) {
		if (from == to)
			return new ArrayList<Location>();
		initialiseMatrix(quadrant, map);
		final int indexFrom = coordsToIndex(from.getX(), from.getY());
		addToDo(indexFrom);
		matrix[indexFrom] = OCCUPIED;
		int indexTo = coordsToIndex(to.getX(), to.getY());
		matrix[indexTo] = FREE;
		while (hasMoreTodo()) {
			int indexNext = getNextTodo();
			if (indexNext == indexTo) {
				return reconstructPath(indexFrom, indexTo);
			}
			addNeighboursToTodo(indexNext);
		}
		return new ArrayList<Location>();
	}
}
