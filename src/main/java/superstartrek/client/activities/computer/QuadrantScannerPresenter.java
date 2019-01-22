package superstartrek.client.activities.computer;

import com.google.gwt.core.client.GWT;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.loading.GameStartedEvent;
import superstartrek.client.activities.loading.GameStartedHandler;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Thing;

public class QuadrantScannerPresenter extends BasePresenter implements SectorSelectedHandler, GameStartedHandler{
	
	public void onSectorSelected(int x, int y) {
		application.events.fireEvent(new SectorSelectedEvent(x, y));
	}

	public QuadrantScannerPresenter(Application application) {
		super(application);
		application.events.addHandler(SectorSelectedEvent.TYPE, this);
		application.events.addHandler(GameStartedEvent.TYPE, this);
	}

	@Override
	public void onSectorSelected(SectorSelectedEvent event) {
		GWT.log("sectorselected");
		((QuadrantScannerActivity)screen).deselectSectors();
		((QuadrantScannerActivity)screen).selectSector(event.x, event.y);
	}
	
	protected void updateScreen(){
		Quadrant q= getApplication().starMap.getQuadrant(0, 0);
		if (q==null)
			throw new RuntimeException("q is null");
		for (int y=0;y<8;y++)
		for (int x=0;x<8;x++) {
			Thing thing = q.thing(x, y);
			String content = thing!=null?"*":"";
			String css = thing!=null?"star":"";
			((QuadrantScannerActivity)screen).updateSector(x, y, content, css);
		}
	}

	@Override
	public void onGameStared(GameStartedEvent evt) {
		updateScreen();
	}
	
}
