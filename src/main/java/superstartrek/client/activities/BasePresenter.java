package superstartrek.client.activities;

import superstartrek.client.Application;
import superstartrek.client.eventbus.Event;
import superstartrek.client.eventbus.EventHandler;
import superstartrek.client.utils.BaseMixin;

@SuppressWarnings("rawtypes")
public abstract class BasePresenter<V extends View> implements Presenter<V>, BaseMixin, EventHandler{

	protected V view;
	
	protected <T extends EventHandler> void addHandler(Event<T> type) {
		getEvents().addHandler(type, (T)this);
	}

	
	@Override
	public void setView(V view) {
		this.view = view;
	}
	
	@Override
	public Application getApplication() {
		return BaseMixin.super.getApplication();
	}
	
}
