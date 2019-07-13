package superstartrek.client.activities.computer.quadrantscanner;

import superstartrek.client.activities.View;
import superstartrek.client.activities.pwa.Callback;

public interface IQuadrantScannerView extends View<QuadrantScannerPresenter>{

	void deselectSectors();

	void selectSector(int x, int y);

	void updateSector(int x, int y, String content, String css);
	
	void removeCssFromCell(int x, int y, String css);
	void addCssToCell(int x, int y, String css);
	int getHorizontalOffsetOfSector(int x, int y);
	int getVerticalOffsetOfSector(int x, int y);
	
	void drawBeamBetween(int x1, int y1, int x2, int y2, String colour);
	void clearBeamMarks();
	void animateTorpedoFireBetween(int x1, int y1, int x2, int y2, Callback<Void> callback);
	
}