package superstartrek.client.model;

public abstract class Vessel extends Thing{

	protected final Setting impulse;
	protected final Setting shields;

	public Setting getImpulse() {
		return impulse;
	}

	public Setting getShields() {
		return shields;
	}
	
	protected Vessel(Setting impulse, Setting shields) {
		this.impulse = impulse;
		this.shields = shields;
	}
	
	public static boolean is(Thing thing) {
		return thing instanceof Vessel;
	}
	
}
