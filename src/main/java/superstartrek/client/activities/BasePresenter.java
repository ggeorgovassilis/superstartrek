package superstartrek.client.activities;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

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
	
	protected <H extends EventHandler> HandlerRegistration addHandler(GwtEvent.Type<H> type, H handler) {
		return application.events.addHandler(type, handler);
	}
}
