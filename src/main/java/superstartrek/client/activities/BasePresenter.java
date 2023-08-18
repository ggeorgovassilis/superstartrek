package superstartrek.client.activities;

import superstartrek.client.Application;
import superstartrek.client.utils.BaseMixin;

@SuppressWarnings("rawtypes")
public abstract class BasePresenter<V extends View> implements Presenter<V>, BaseMixin{

	protected V view;
	
	@Override
	public void setView(V view) {
		this.view = view;
	}
	
	@SuppressWarnings("unchecked")
	protected void addViewToRoot() {
		getApplication().browserAPI.addToPage(view);
	}
	
	@Override
	public Application getApplication() {
		return BaseMixin.super.getApplication();
	}
	
}
