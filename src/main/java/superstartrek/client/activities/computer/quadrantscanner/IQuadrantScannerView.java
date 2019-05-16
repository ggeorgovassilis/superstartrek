package superstartrek.client.activities.computer.quadrantscanner;

import superstartrek.client.activities.View;
import superstartrek.client.control.KeyPressedEventHandler;
import superstartrek.client.model.Location;

public interface IQuadrantScannerView extends View{

	void deselectSectors();

	void selectSector(int x, int y);

	void updateSector(int x, int y, String content, String css);
	
	void removeCssFromCell(int x, int y, String css);
	void addCssToCell(int x, int y, String css);
	int getHorizontalOffsetOfSector(int x, int y);
	int getVerticalOffsetOfSector(int x, int y);
	
}