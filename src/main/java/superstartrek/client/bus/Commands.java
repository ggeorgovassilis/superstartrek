package superstartrek.client.bus;

import superstartrek.client.activities.appmenu.AppMenuHandler;
import superstartrek.client.activities.pwa.ApplicationLifecycleHandler;

public class Commands {

	public final static Event<AppMenuHandler> APP_MENU_SHOW = new Event<>("APP_MENU_SHOW");
	public final static Event<AppMenuHandler> APP_MENU_HIDE = new Event<>("APP_MENU_HIDE");
	public final static Event<ApplicationLifecycleHandler> RELOAD_APP = new Event<>("RELOAD_APP");

}
