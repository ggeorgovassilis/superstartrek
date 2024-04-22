package superstartrek.client.activities;

import superstartrek.client.eventbus.EventHandler;
import superstartrek.client.eventbus.EventsMixin;
import superstartrek.client.utils.BaseMixin;

@SuppressWarnings("rawtypes")
public abstract class BasePresenter<V extends View> implements Presenter<V>, BaseMixin, EventsMixin, EventHandler{

	protected V view;
	
	@Override
	public void setView(V view) {
		this.view = view;
	}
	
}
