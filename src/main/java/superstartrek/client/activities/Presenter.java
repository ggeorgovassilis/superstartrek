package superstartrek.client.activities;

import superstartrek.client.Application;

public interface Presenter {

	Application getApplication();
	void setView(View<? extends Presenter> view);
}
