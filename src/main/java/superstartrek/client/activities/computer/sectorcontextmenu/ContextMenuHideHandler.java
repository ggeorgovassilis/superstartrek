package superstartrek.client.activities.computer.sectorcontextmenu;

import superstartrek.client.eventbus.EventHandler;

public interface ContextMenuHideHandler extends EventHandler{
	default void onMenuHidden(){};
	default void onStartToHideMenu() {};
}
