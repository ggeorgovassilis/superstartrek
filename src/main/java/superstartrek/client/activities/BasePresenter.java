package superstartrek.client.activities;

import superstartrek.client.Application;

public abstract class BasePresenter implements Presenter{

	protected Screen screen;
	protected Application application;
	
	protected BasePresenter(Application application) {
		this.application = application;
	}
	
	public Screen getScreen() {
		return screen;
	}
	
	public Application getApplication() {
		return application;
	}
	
	
	public void setScreen(Screen screen) {
		this.screen = screen;
	}
}
