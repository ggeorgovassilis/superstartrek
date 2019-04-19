package superstartrek.client.model;

public interface GeometricLookup {

	Thing findThingAt(int x, int y);
	Thing findThingAt(Location location);
}
