package superstartrek.client.activities;

import superstartrek.client.Application;

public abstract class BasePresenter<A extends Activity> implements Presenter<A>{

	protected View<A> view;
	protected Application application;
	
	protected BasePresenter(Application application) {
		this.application = application;
	}
	
	public <T extends View<A>> T getView() {
		return (T)view;
	}

	
	@Override
	public Application getApplication() {
		return application;
	}
	
	
	@Override
	public void setView(View<A> view) {
		this.view = view;
	}
}
