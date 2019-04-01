package superstartrek.client.model;

public abstract class Thing {

	protected String name;
	protected String symbol;
	protected Quadrant quadrant;
	protected String css; //TODO: the code often concatenates CSS like thing.css+" "+somthingElse. If css already contained the trailing space, we could cut some GC?
	protected Location location = Location.location(0,0);

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
		if (location == null)
			throw new IllegalArgumentException("location is null");
		this.location = l;
	}
	
	public Location getLocation() {
		return location;
	}
}
