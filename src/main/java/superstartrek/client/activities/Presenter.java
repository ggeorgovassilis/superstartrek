package superstartrek.client.activities;

import superstartrek.client.Application;

public interface Presenter<V extends View<?>> {

	Application getApplication();
	void setView(V view);
}
