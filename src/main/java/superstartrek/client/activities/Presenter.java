package superstartrek.client.activities;

import superstartrek.client.Application;

@SuppressWarnings("rawtypes")
public interface Presenter<V extends View> {

	void setView(V view);
	Application getApplication();
	
}
