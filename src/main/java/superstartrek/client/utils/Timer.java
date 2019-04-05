package superstartrek.client.utils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public class Timer {

	public static void postpone(ScheduledCommand cmd) {
		if (GWT.isClient())
			Scheduler.get().scheduleDeferred(cmd);
	};
	
	public static void postpone(RepeatingCommand cmd, int ms) {
		if (GWT.isClient())
			Scheduler.get().scheduleFixedDelay(cmd, ms);
	};
}
