package superstartrek.client.activities.lrs;

import superstartrek.client.activities.View;
import superstartrek.client.activities.computer.srs.MapCellRenderer;

public interface ILRSScreen extends View<LRSPresenter>, MapCellRenderer{

	void addCss(int x, int y, String css);
	void focusCell(int x, int y);

}