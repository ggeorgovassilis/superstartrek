package superstartrek.client.utils;

import com.google.gwt.core.client.GWT;

public class Timer {

	public static void postpone(Runnable r) {
		if (GWT.isClient())
			new com.google.gwt.user.client.Timer() {

				@Override
				public void run() {
					r.run();
				}
			}.schedule(1);
		else {
			GWT.log("Unit test postponing call");
		}
	};
}
