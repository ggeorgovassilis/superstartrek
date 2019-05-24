package superstartrek.client.activities;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.IsWidget;

@SuppressWarnings("rawtypes")
public interface View<P extends Presenter> extends IsWidget{

	void show();
	void hide();
	void hide(ScheduledCommand callback);
	boolean isVisible();
}
