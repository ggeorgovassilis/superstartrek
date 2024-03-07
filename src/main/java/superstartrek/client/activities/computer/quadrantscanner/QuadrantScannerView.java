package superstartrek.client.activities.computer.quadrantscanner;

import superstartrek.client.activities.View;

public interface QuadrantScannerView extends View<QuadrantScannerPresenter>{

	void deselectSectors();

	void selectSector(int x, int y);

	void updateSector(int x, int y, String content, String css);
	
	void clearSector(int x, int y);
	
	void removeCssFromCell(int x, int y, String css);
	void addCssToCell(int x, int y, String css);
	
	void drawBeamBetween(int x1, int y1, int x2, int y2, String colour);
	void clearBeamMarks();
	
	int getAbsoluteTop();
	
	int getAbsoluteLeft();
	
	int[] getCoordinatesOfElement(String id);
	
}