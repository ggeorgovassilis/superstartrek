package superstartrek.client.model;

public abstract class Thing extends Location{

	protected String name;
	protected String symbol;
	protected Quadrant quadrant;
	protected String css;
	
	public void setCss(String css) {
		this.css = css;
	}
	
	public String getCss() {
		return css;
	}
	
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
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