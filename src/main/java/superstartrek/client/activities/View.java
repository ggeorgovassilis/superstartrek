package superstartrek.client.activities;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;

@SuppressWarnings("rawtypes")
public interface View<P extends Presenter> {

	void show();
	void hide();
	void hide(ScheduledCommand callback);
	boolean isVisible();
	P getPresenter();
}
