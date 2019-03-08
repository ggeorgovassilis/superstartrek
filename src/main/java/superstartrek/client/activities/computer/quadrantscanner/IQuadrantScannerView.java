package superstartrek.client.activities.computer.quadrantscanner;

import superstartrek.client.activities.Activity;
import superstartrek.client.activities.IBaseView;

public interface IQuadrantScannerView<A extends Activity> extends IBaseView<A>{

	void deselectSectors();

	void selectSector(int x, int y);

	void updateSector(int x, int y, String content, String css);

	void setQuadrantHeader(String name, String css);

}