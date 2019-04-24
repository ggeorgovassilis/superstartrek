package superstartrek.client.activities;

import superstartrek.client.Application;

public abstract class BasePresenter<V extends View> implements Presenter<V>{

	protected V view;
	protected final Application application;
	
	protected BasePresenter(Application application) {
		this.application = application;
	}
	
	@Override
	public Application getApplication() {
		return application;
	}
	
	
	@Override
	public void setView(V view) {
		this.view = view;
	}
}
