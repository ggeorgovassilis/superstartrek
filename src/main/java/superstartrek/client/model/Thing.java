package superstartrek.client.model;

public abstract class Thing {

	protected String name;
	protected int x;
	protected int y;
	protected Quadrant quadrant;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public Quadrant getQuadrant() {
		return quadrant;
	}
	public void setQuadrant(Quadrant quadrant) {
		this.quadrant = quadrant;
	}
}
