package superstartrek.client.activities.computer.quadrantscanner;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.CSS;
import superstartrek.client.activities.combat.FireEvent;
import superstartrek.client.activities.combat.FireHandler;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.KlingonDestroyedEvent;
import superstartrek.client.activities.klingons.KlingonDestroyedHandler;
import superstartrek.client.activities.klingons.KlingonUncloakedEvent;
import superstartrek.client.activities.klingons.KlingonUncloakedHandler;
import superstartrek.client.activities.loading.GameStartedEvent;
import superstartrek.client.activities.loading.GameStartedHandler;
import superstartrek.client.activities.navigation.EnterpriseRepairedEvent;
import superstartrek.client.activities.navigation.EnterpriseRepairedHandler;
import superstartrek.client.activities.navigation.EnterpriseWarpedEvent;
import superstartrek.client.activities.navigation.EnterpriseWarpedHandler;
import superstartrek.client.activities.navigation.ThingMovedEvent;
import superstartrek.client.activities.navigation.ThingMovedHandler;
import superstartrek.client.activities.sector.contextmenu.SectorMenuPresenter;
import superstartrek.client.activities.sector.contextmenu.SectorMenuView;
import superstartrek.client.activities.sector.contextmenu.SectorSelectedEvent;
import superstartrek.client.activities.sector.contextmenu.SectorSelectedHandler;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;

public class QuadrantScannerPresenter extends BasePresenter<QuadrantScannerActivity> implements SectorSelectedHandler, GameStartedHandler, ThingMovedHandler, EnterpriseWarpedHandler, FireHandler, EnterpriseRepairedHandler, KlingonUncloakedHandler, KlingonDestroyedHandler {

	SectorMenuPresenter sectorMenuPresenter;
	
	
	public void onSectorSelected(int x, int y, int screenX, int screenY) {
		application.events.fireEvent(new SectorSelectedEvent(new Location(x,y), application.starMap.enterprise.getQuadrant(), screenX, screenY));
	}

	public QuadrantScannerPresenter(Application application, SectorMenuPresenter sectorMenuPresenter) {
		super(application);
		application.events.addHandler(SectorSelectedEvent.TYPE, this);
		application.events.addHandler(GameStartedEvent.TYPE, this);
		application.events.addHandler(ThingMovedEvent.TYPE, this);
		application.events.addHandler(EnterpriseWarpedEvent.TYPE, this);
		application.events.addHandler(FireEvent.TYPE, this);
		application.events.addHandler(EnterpriseRepairedEvent.TYPE, this);
		application.events.addHandler(KlingonDestroyedEvent.TYPE, this);
		application.events.addHandler(KlingonUncloakedEvent.TYPE, this);
		this.sectorMenuPresenter = sectorMenuPresenter;
	}

	@Override
	public void onSectorSelected(SectorSelectedEvent event) {
		((IQuadrantScannerView) getView()).deselectSectors();
		((IQuadrantScannerView) getView()).selectSector(event.sector.getX(), event.sector.getY());
	}
	
	protected void updateSector(Quadrant q, int x, int y) {
		StarMap starMap = getApplication().starMap;
		Thing thing = starMap.findThingAt(q, x, y);
		String content = "";
		String css = "";
		if (thing!=null) {
			content = thing.getSymbol();
			css = thing.getCss();
			if (thing instanceof Vessel) {
				Vessel vessel = (Vessel)thing;
				double status = vessel.getShields().health();
				css+=" "+CSS.damageClass(status);
			}
		}
		((IQuadrantScannerView) view).updateSector(x, y, content, css);
	}

	protected void updateScreen() {
		StarMap starMap = getApplication().starMap;
		Quadrant q = starMap.enterprise.getQuadrant();
		if (q == null)
			throw new RuntimeException("q is null");
		for (int y = 0; y < 8; y++)
			for (int x = 0; x < 8; x++) {
				updateSector(q, x,y);
			}
		updateQuadrantHeader();
	}

	@Override
	public void onGameStared(GameStartedEvent evt) {
		updateScreen();
	}

	@Override
	public void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
		updateSector(qFrom, lFrom.getX(), lFrom.getY());
		updateSector(qTo, lTo.getX(), lTo.getY());
		updateQuadrantHeader();
	}

	@Override
	public void onEnterpriseWarped(Enterprise enterprise, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
		updateScreen();
	}

	@Override
	public void onEnterpriseRepaired() {
		Enterprise enterprise = application.starMap.enterprise;
		updateSector(enterprise.getQuadrant(), enterprise.getLocation().getX(), enterprise.getLocation().getY());
	}

	@Override
	public void klingonUncloaked(Klingon klingon) {
		updateSector(klingon.getQuadrant(), klingon.getLocation().getX(), klingon.getLocation().getY());
	}

	@Override
	public void klingonDestroyed(Klingon klingon) {
		updateSector(klingon.getQuadrant(), klingon.getLocation().getX(), klingon.getLocation().getY());
		updateQuadrantHeader();
	}
	
	public void updateQuadrantHeader() {
		IQuadrantScannerView view = (IQuadrantScannerView)getView();
		String alert = "";
		Quadrant q = application.starMap.enterprise.getQuadrant();
		Enterprise e = application.starMap.enterprise;
		if (!q.getKlingons().isEmpty()) {
			alert = "yellow-alert";
			double minDistance = 3;
			for (Klingon k:q.getKlingons())
				minDistance = Math.min(minDistance, StarMap.distance(e, k));
			if (minDistance<3)
				alert="red-alert";
		}
		
		view.setQuadrantHeader(q.getName(), alert);
	}
	
	
	@Override
	public void onFire(Vessel actor, Thing target, String weapon, double damage) {
	}


	@Override
	public void afterFire(Vessel actor, Thing target, String weapon, double damage) {
		updateSector(target.getQuadrant(), target.getLocation().getX(), target.getLocation().getY());
	}


}
