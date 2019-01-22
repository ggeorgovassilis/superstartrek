package superstartrek.client.activities;

import superstartrek.client.Application;

public abstract class BasePresenter implements Presenter{

	protected Activity screen;
	protected Application application;
	
	protected BasePresenter(Application application) {
		this.application = application;
	}
	
	public Activity getScreen() {
		return screen;
	}
	
	public Application getApplication() {
		return application;
	}
	
	
	public void setScreen(Activity screen) {
		this.screen = screen;
	}
}
