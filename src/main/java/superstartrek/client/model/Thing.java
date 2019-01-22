package superstartrek.client.model;

public abstract class Thing extends Location{

	protected String name;
	protected Quadrant quadrant;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Quadrant getQuadrant() {
		return quadrant;
	}
	public void setQuadrant(Quadrant quadrant) {
		this.quadrant = quadrant;
	}
	
	public void setLocation(Location l) {
		setX(l.getX());
		setY(l.getY());
	}
}
