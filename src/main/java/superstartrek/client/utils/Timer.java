package superstartrek.client.utils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public abstract class Timer {
	
	public static void postpone(ScheduledCommand cmd) {
		if (GWT.isClient())
			Scheduler.get().scheduleDeferred(cmd);
		else
			cmd.execute();
	};

	public static void postpone(final ScheduledCommand cmd, int ms) {
		if (GWT.isClient())
			Scheduler.get().scheduleFixedDelay(() -> {
				cmd.execute();
				return false;
			}, ms);
		else
			cmd.execute();
	}

}
