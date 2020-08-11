package superstartrek.client.model;

public abstract class Thing {

	protected String name;
	protected String symbol;
	protected String css; //TODO: the code often concatenates CSS like thing.css+" "+somthingElse. If css already contained the trailing space, we could cut some GC?
	protected Location location = Location.location(0,0);
	
	@SuppressWarnings("unchecked")
	public <T> T as() {
		return (T)this;
	}
	
	public static boolean isVisible(Thing thing) {
		return thing!=null && thing.isVisible();
	}
	
	public boolean isVisible() {
		return true;
	}

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

	public void setLocation(Location l) {
		if (location == null)
			throw new IllegalArgumentException("location is null");
		this.location = l;
	}
	
	public Location getLocation() {
		return location;
	}
}
