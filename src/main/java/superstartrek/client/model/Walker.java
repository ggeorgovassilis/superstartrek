package superstartrek.client.model;

public interface Walker {

	// return true to continue
	boolean visit(Quadrant q, int x, int y);
}
