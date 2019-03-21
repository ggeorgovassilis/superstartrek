package superstartrek.client.activities.navigation.astarplus;

import java.util.ArrayList;
import java.util.List;

import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;

public class AStarPlus {

	final static int OCCUPIED = -1;
	final static int FREE = 0;
	int[] matrix = new int[65]; // 0 is never used, look into code for explanation
	int[] todo = new int[64];
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
		for (Thing obstacle : quadrant.getKlingons())
			markOccupied(obstacle);
		for (Thing obstacle : quadrant.getStars())
			markOccupied(obstacle);
		if (quadrant.getStarBase() != null)
			markOccupied(quadrant.getStarBase());
		// null only in some unit tests
		if (map.enterprise != null)
			markOccupied(map.enterprise);
	}

	void addNeighboursToTodo(int sectorIndex) {
		int sectorX = indexToX(sectorIndex);
		int sectorY = indexToY(sectorIndex);

		int minX = Math.max(0, sectorX - 1);
		int maxX = Math.min(7, sectorX + 1);
		int minY = Math.max(0, sectorY - 1);
		int maxY = Math.min(7, sectorY + 1);
		for (int y = minY; y <= maxY; y++) {
			int index = coordsToIndex(minX, y);
			for (int x = minX; x <= maxX; x++) {
				if (index != sectorIndex) {
					int c = matrix[index];
					if (c == FREE) {
						matrix[index] = sectorIndex;
						addToDo(index);
					}
				}
				index++;
			}
		}
	}

	List<Location> reconstructPath(int indexTo) {
		List<Location> path = new ArrayList<Location>();
		int previousIndex = indexTo;
		while (previousIndex != OCCUPIED) {
			int x = indexToX(previousIndex);
			int y = indexToY(previousIndex);
			path.add(0, Location.location(x, y));
			previousIndex = matrix[previousIndex];
		}
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

	public List<Location> findPathBetween(Location from, Location to, Quadrant quadrant, StarMap map) {
		if (from == to)
			return new ArrayList<Location>();
		initialiseMatrix(quadrant, map);
		int indexFrom = coordsToIndex(from.getX(), from.getY());
		addToDo(indexFrom);
		matrix[indexFrom] = OCCUPIED;
		int indexTo = coordsToIndex(to.getX(), to.getY());
		matrix[indexTo] = FREE;
		while (hasMoreTodo()) {
			int indexNext = getNextTodo();
			if (indexNext == indexTo) {
				return reconstructPath(indexTo);
			}
			addNeighboursToTodo(indexNext);
		}
		return new ArrayList<Location>();
	}
}
