package superstartrek.client.activities.computer.srs;

import superstartrek.client.activities.IBaseView;

public interface ISRSView extends IBaseView<SRSActivity>, MapCellRenderer{

	void finishUiConstruction();

	void updateCell(int x, int y, String symbol, String css);

}