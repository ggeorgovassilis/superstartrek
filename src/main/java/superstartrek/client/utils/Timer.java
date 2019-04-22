package superstartrek.client.utils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class Timer {

	public static void postpone(ScheduledCommand cmd) {
		if (GWT.isClient())
			Scheduler.get().scheduleDeferred(cmd);
		else cmd.execute();
	};
	
	public static void postpone(RepeatingCommand cmd, int ms) {
		if (GWT.isClient())
			Scheduler.get().scheduleFixedDelay(cmd, ms);
		else cmd.execute();
	}
	
	public static void fireAsync(EventBus bus, GwtEvent<EventHandler> event) {
		postpone(new ScheduledCommand() {
			
			@Override
			public void execute() {
				bus.fireEvent(event);
			}
		});
	}

}
