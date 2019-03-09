package superstartrek.client.activities.lrs;

import superstartrek.client.activities.IBaseView;
import superstartrek.client.activities.computer.srs.MapCellRenderer;

public interface ILRSScreen extends IBaseView<LRSActivity>, MapCellRenderer{

	void finishUiConstruction();

	void addCss(int x, int y, String css);

	void updateCell(int x, int y, String text, String css);

}