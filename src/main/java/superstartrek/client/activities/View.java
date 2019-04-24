package superstartrek.client.activities;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public interface View {

	void show();
	void hide();
	void hide(ScheduledCommand callback);
	boolean isVisible();
}
