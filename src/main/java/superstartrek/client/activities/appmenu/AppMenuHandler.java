package superstartrek.client.activities.appmenu;

import superstartrek.client.bus.EventHandler;

public interface AppMenuHandler extends EventHandler {

	default void showMenu() {
	}

	default void hideMenu() {
	}
}
