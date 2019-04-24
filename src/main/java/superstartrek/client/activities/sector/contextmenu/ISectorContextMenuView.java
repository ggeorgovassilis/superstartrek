package superstartrek.client.activities.sector.contextmenu;

import superstartrek.client.activities.View;

public interface ISectorContextMenuView extends View {

	void setLocation(int x, int y);

	void enableButton(String id, boolean status);
	
	int getMetricWidthInPx();
	int getMetricHeightInPx();

}