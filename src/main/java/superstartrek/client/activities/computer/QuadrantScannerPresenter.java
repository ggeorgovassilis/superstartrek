package superstartrek.client.activities.computer;

import com.google.gwt.core.client.GWT;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;

public class QuadrantScannerPresenter extends BasePresenter implements SectorSelectedHandler{
	
	public void onSectorSelected(int x, int y) {
		application.events.fireEvent(new SectorSelectedEvent(x, y));
	}

	public QuadrantScannerPresenter(Application application) {
		super(application);
		application.events.addHandler(SectorSelectedEvent.TYPE, this);
	}

	@Override
	public void onSectorSelected(SectorSelectedEvent event) {
		GWT.log("sectorselected");
		((QuadrantScannerActivity)screen).deselectSectors();
		((QuadrantScannerActivity)screen).selectSector(event.x, event.y);
	}
	
}
