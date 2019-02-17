package superstartrek.client.model;

import superstartrek.client.Application;
import superstartrek.client.activities.loading.GameOverEvent;
import superstartrek.client.activities.loading.GameOverEvent.Outcome;

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
	
	
	public void destroy() {
	}
}
