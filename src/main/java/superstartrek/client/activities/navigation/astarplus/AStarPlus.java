package superstartrek.client.activities.navigation.astarplus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;

public class AStarPlus {
	
	final static int OCCUPIED = -1;

	/**
	 * 0 means traversable and not visited yet
	 * -1 means not traversable
	 * anything else means visited where the value is the path length
	 */
	int[][] matrix;
	
	void markOccupied(int[][] matrix, Thing thing) {
		Location l = thing.getLocation();
		matrix[l.getX()][l.getY()] = OCCUPIED;
	}
	
	int[][] initialiseMatrix(Quadrant quadrant, StarMap map) {
		matrix = new int[8][8];
		for (Thing obstacle:quadrant.getKlingons())
			markOccupied(matrix, obstacle);
		for (Thing obstacle:quadrant.getStars())
			markOccupied(matrix, obstacle);
		if (quadrant.getStarBase()!=null)
			markOccupied(matrix, quadrant.getStarBase());
		//null only in some unit tests
		if (map.enterprise!=null)
			markOccupied(matrix, map.enterprise);
		return matrix;
	}
	
	void addNeighboursToTodo(Location lastStep, List<Location> todo, int[][] matrix, int newPathLength) {
		int minX = Math.max(0, lastStep.getX()-1);
		int maxX = Math.min(7, lastStep.getX()+1);
		int minY = Math.max(0, lastStep.getY()-1);
		int maxY = Math.min(7, lastStep.getY()+1);
		for (int x=minX;x<=maxX;x++)
			for (int y=minY;y<=maxY;y++) {
				int c = matrix[x][y];
				if (c==0 || c>newPathLength) {
					matrix[x][y] = newPathLength;
					Location toVisit = Location.location(x, y);
					if (!todo.contains(toVisit))
						todo.add(toVisit);
				}
			}
	}
	
	List<Location> _findPath(Location to, int currentPathLength, List<Location> todo, int[][] matrix){
		List<Location> path = new ArrayList<Location>();
		while (!todo.isEmpty()) {
			Location next = todo.remove(0);
			matrix[next.getX()][next.getY()] = currentPathLength+1;
			addNeighboursToTodo(next, todo, matrix, currentPathLength+1);
			List<Location> subPath = _findPath(to, currentPathLength+1, todo, matrix);
			if (!subPath.isEmpty()) {
				path.addAll(subPath);
				return path;
			}
		}
		return path;
	}
	
	
	
	public List<Location> findPathBetween(Location from, Location to, Quadrant quadrant, StarMap map){
		matrix = initialiseMatrix(quadrant, map);
		List<Location> todo = new ArrayList<Location>();
		todo.add(from);
		return _findPath(to, 0, todo, matrix);
	}
}
