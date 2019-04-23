package superstartrek.client.activities;

import superstartrek.client.Application;

public abstract class BasePresenter implements Presenter{

	protected View<? extends Presenter> view;
	protected Application application;
	
	protected BasePresenter(Application application) {
		this.application = application;
	}
	
	@SuppressWarnings("unchecked")
	public <V extends View<? extends Presenter>> V  getView() {
		return (V)view;
	}

	
	@Override
	public Application getApplication() {
		return application;
	}
	
	
	@Override
	public void setView(View<? extends Presenter> view) {
		this.view = view;
	}
}
