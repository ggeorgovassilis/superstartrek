package superstartrek.client.activities;

import superstartrek.client.Application;
import superstartrek.client.bus.BaseHandler;
import superstartrek.client.bus.Event;

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
	
	protected <H extends BaseHandler> void addHandler(Event<H> type, H handler) {
		application.eventBus.addHandler(type, handler);
	}
}
