package superstartrek.client.activities.sector.contextmenu;

import superstartrek.client.activities.IBaseView;

public interface ISectorContextMenuView extends IBaseView<SectorContextMenuPresenter> {

	void setLocation(int x, int y);

	void enableButton(String id, boolean status);
	
	int getMetricWidthInPx();
	int getMetricHeightInPx();

}