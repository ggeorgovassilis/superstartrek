package superstartrek.client.activities.computer.quadrantscanner;

import superstartrek.client.activities.View;

public interface IQuadrantScannerView extends View<QuadrantScannerPresenter>{

	void deselectSectors();

	void selectSector(int x, int y);

	void updateSector(int x, int y, String content, String css);
	
	void removeCssFromCell(int x, int y, String css);
	void addCssToCell(int x, int y, String css);
	int getHorizontalOffsetOfSector(int x, int y);
	int getVerticalOffsetOfSector(int x, int y);
	
}