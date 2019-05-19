package superstartrek.client.activities.appmenu;

import superstartrek.client.bus.BaseHandler;

public interface AppMenuHandler extends BaseHandler {

	default void showMenu() {
	}

	default void hideMenu() {
	}
}
