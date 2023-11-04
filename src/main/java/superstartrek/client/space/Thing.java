package superstartrek.client.space;

public abstract class Thing {

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
		assert(l!=null);
		this.location = l;
	}
	
	public Location getLocation() {
		return location;
	}
}
