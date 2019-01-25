package superstartrek.client.activities.computer.quadrantscanner;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.loading.GameStartedEvent;
import superstartrek.client.activities.loading.GameStartedHandler;
import superstartrek.client.activities.sector.contextmenu.SectorMenuPresenter;
import superstartrek.client.activities.sector.contextmenu.SectorMenuView;
import superstartrek.client.activities.sector.contextmenu.SectorSelectedEvent;
import superstartrek.client.activities.sector.contextmenu.SectorSelectedHandler;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;

public class QuadrantScannerPresenter extends BasePresenter<QuadrantScannerActivity> implements SectorSelectedHandler, GameStartedHandler {

	SectorMenuPresenter sectorMenuPresenter;
	
	
	public void onSectorSelected(int x, int y, int screenX, int screenY) {
		application.events.fireEvent(new SectorSelectedEvent(new Location(x,y), application.starMap.enterprise.getQuadrant(), screenX, screenY));
	}

	public QuadrantScannerPresenter(Application application) {
		super(application);
		application.events.addHandler(SectorSelectedEvent.TYPE, this);
		application.events.addHandler(GameStartedEvent.TYPE, this);
		sectorMenuPresenter = new SectorMenuPresenter(application);
		sectorMenuPresenter.setView(new SectorMenuView(sectorMenuPresenter));
	}

	@Override
	public void onSectorSelected(SectorSelectedEvent event) {
		((QuadrantScannerView) getView()).deselectSectors();
		((QuadrantScannerView) getView()).selectSector(event.sector.getX(), event.sector.getY());
	}

	protected void updateScreen() {
		StarMap starMap = getApplication().starMap;
		Quadrant q = starMap.getQuadrant(0, 0);
		if (q == null)
			throw new RuntimeException("q is null");
		for (int y = 0; y < 8; y++)
			for (int x = 0; x < 8; x++) {
				Thing thing = starMap.findThingAt(q, x, y);
				String content = "";
				String css = "";
				if (thing!=null) {
					content = thing.getSymbol();
					css = thing.getCss();
				}
				((QuadrantScannerView) view).updateSector(x, y, content, css);
			}
	}

	@Override
	public void onGameStared(GameStartedEvent evt) {
		updateScreen();
	}

}
