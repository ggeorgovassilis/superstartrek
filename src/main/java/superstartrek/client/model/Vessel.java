package superstartrek.client.model;

import superstartrek.client.Application;

public abstract class Vessel extends Thing{

	protected final Application application;
	protected final Setting impulse;
	protected final Setting shields;

	public Setting getImpulse() {
		return impulse;
	}

	public Setting getShields() {
		return shields;
	}
	
	public Application getApplication() {
		return application;
	}

	protected Vessel(Application application, Setting impulse, Setting shields) {
		this.application = application;
		this.impulse = impulse;
		this.shields = shields;
	}
}
