package superstartrek.client.activities;

import superstartrek.client.Application;

public interface Presenter<A extends Activity> {

	Application getApplication();
	void setView(View<A> view);
}
