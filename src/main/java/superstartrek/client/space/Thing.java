package superstartrek.client.space;

public abstract class Thing {

	protected String css; //TODO: the code often concatenates CSS like thing.css+" "+somthingElse. If css already contained the trailing space, we could cut some GC?
	protected Location location = Location.location(0,0);
	
	@SuppressWarnings("unchecked")
	public <T extends Thing> T as() {
		return (T)this;
	}
	
	public static boolean isVisible(Thing thing) {
		return thing!=null && thing.isVisible();
	}
	
	public boolean isVisible() {
		return true;
	}

	public abstract String getCss();

	public abstract String getSymbol();

	public abstract String getName();

	public void setLocation(Location l) {
		if (location == null)
			throw new IllegalArgumentException("location is null");
		this.location = l;
	}
	
	public Location getLocation() {
		return location;
	}
}
