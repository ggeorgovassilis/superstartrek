package superstartrek.client.activities.navigation.astarplus;

import java.util.ArrayList;
import java.util.List;

import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;

public class AStarPlus {
	
	final static int OCCUPIED = -1;
	final static int FREE=0;
	int[][] matrix;
	int[] todo = new int[64]; //32 probably also enough
	int todoHead = 0;
	int todoTail = 0;
	
	int coordsToIndex(int x, int y) {
		// need to add 1 because "0" (0*8+8) means "free"
		return 1+x+y*8;
	}

	int indexToX(int index) {
		return (index-1) % 8;
	}
	
	int indexToY(int index) {
		return (index-1)/8;
	}
	
	void addToDo(int index) {
		todo[todoTail++]=index;
	}
	
	int getNextTodo() {
		if (todoHead>=todoTail)
			throw new IndexOutOfBoundsException("Head after tail "+todoHead+" "+todoTail);
		return todo[todoHead++];
	}
	
	boolean hasMoreTodo() {
		return todoTail>todoHead;
	}
	
	
	/**
	 * 0 means traversable and not visited yet
	 * -1 means not traversable
	 * anything else means visited where the value is the index of the sector
	 * we came from
	 */
	
	void markOccupied(Thing thing) {
		Location l = thing.getLocation();
		matrix[l.getX()][l.getY()] = OCCUPIED;
	}
	
	int[][] initialiseMatrix(Quadrant quadrant, StarMap map) {
		matrix = new int[8][8];
		for (int x=0;x<8;x++)
			for (int y=0;y<8;y++)
				matrix[x][y]=FREE;
		for (Thing obstacle:quadrant.getKlingons())
			markOccupied(obstacle);
		for (Thing obstacle:quadrant.getStars())
			markOccupied(obstacle);
		if (quadrant.getStarBase()!=null)
			markOccupied(quadrant.getStarBase());
		//null only in some unit tests
		if (map.enterprise!=null)
			markOccupied(map.enterprise);
		return matrix;
	}
	
	void addNeighboursToTodo(int sectorIndex) {
		int sectorX = indexToX(sectorIndex);
		int sectorY = indexToY(sectorIndex);
		
		int minX = Math.max(0, sectorX-1);
		int maxX = Math.min(7, sectorX+1);
		int minY = Math.max(0, sectorY-1);
		int maxY = Math.min(7, sectorY+1);
		for (int x=minX;x<=maxX;x++)
			for (int y=minY;y<=maxY;y++) 
			if (x!=sectorX || y!=sectorY){
				int c = matrix[x][y];
				if (c==FREE) {
					matrix[x][y] = sectorIndex;
					addToDo(coordsToIndex(x, y));
				}
			}
	}
	
	List<Location> reconstructPath(int indexTo){
		List<Location> path = new ArrayList<Location>();
		int x = indexToX(indexTo);
		int y = indexToY(indexTo);
		int previousIndex = matrix[x][y];
		while(previousIndex!=OCCUPIED) {
			x = indexToX(previousIndex);
			y = indexToY(previousIndex);
			path.add(0,Location.location(x, y));
			previousIndex = matrix[x][y];
		}
		return path;
	}
	
	
	
	public List<Location> findPathBetween(Location from, Location to, Quadrant quadrant, StarMap map){
		matrix = initialiseMatrix(quadrant, map);
		addToDo(coordsToIndex(from.getX(), from.getY()));
		matrix[from.getX()][from.getY()]=OCCUPIED;
		
		int indexTo = coordsToIndex(to.getX(), to.getY());
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
