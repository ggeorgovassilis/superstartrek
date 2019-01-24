package superstartrek.client.activities.computer;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.loading.GameStartedEvent;
import superstartrek.client.activities.loading.GameStartedHandler;
import superstartrek.client.activities.sector.SectorMenuPresenter;
import superstartrek.client.activities.sector.SectorMenuView;
import superstartrek.client.activities.sector.SectorSelectedEvent;
import superstartrek.client.activities.sector.SectorSelectedHandler;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;

public class QuadrantScannerPresenter extends BasePresenter<QuadrantScannerActivity> implements SectorSelectedHandler, GameStartedHandler {

	SectorMenuPresenter sectorMenuPresenter;
	
	public void onSectorSelected(int x, int y, int screenX, int screenY) {
		application.events.fireEvent(new SectorSelectedEvent(x, y, screenX, screenY));
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
		((QuadrantScannerView) getView()).selectSector(event.x, event.y);
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
