package superstartrek.client.bus;

import superstartrek.client.activities.pwa.ApplicationLifecycleHandler;

public class Commands {

	public final static Event<ApplicationLifecycleHandler> RELOAD_APP = new Event<>("RELOAD_APP");

}
