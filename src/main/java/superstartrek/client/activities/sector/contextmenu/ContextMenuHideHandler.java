package superstartrek.client.activities.sector.contextmenu;

import superstartrek.client.eventbus.EventHandler;

public interface ContextMenuHideHandler extends EventHandler{
	default void onMenuHidden(){};
	default void onStartToHideMenu() {};
}
