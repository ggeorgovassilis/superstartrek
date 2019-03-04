package superstartrek.client.utils;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public class Timer {

	public static void postpone(ScheduledCommand cmd) {
		Scheduler.get().scheduleDeferred(cmd);
	};
}
