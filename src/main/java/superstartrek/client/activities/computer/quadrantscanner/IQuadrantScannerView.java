package superstartrek.client.activities.computer.quadrantscanner;

import superstartrek.client.activities.IBaseView;

public interface IQuadrantScannerView extends IBaseView<QuadrantScannerActivity>{

	void deselectSectors();

	void selectSector(int x, int y);

	void updateSector(int x, int y, String content, String css);
	
	void removeCssFromCell(int x, int y, String css);
	void addCssToCell(int x, int y, String css);

}