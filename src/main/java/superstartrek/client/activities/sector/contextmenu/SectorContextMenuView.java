package superstartrek.client.activities.sector.contextmenu;

import superstartrek.client.activities.View;

public interface SectorContextMenuView extends View<SectorContextMenuPresenter> {

	void setLocation(int x, int y);

	void enableButton(String id, boolean status);
	
	void enableDockWithStarbaseButton(boolean status);
}