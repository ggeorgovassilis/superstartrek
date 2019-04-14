package superstartrek.client.activities.computer.quadrantscanner;

import java.util.List;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.CSS;
import superstartrek.client.activities.combat.FireHandler;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.KlingonDestroyedHandler;
import superstartrek.client.activities.klingons.KlingonUncloakedHandler;
import superstartrek.client.activities.navigation.EnterpriseRepairedEvent;
import superstartrek.client.activities.navigation.EnterpriseRepairedHandler;
import superstartrek.client.activities.navigation.EnterpriseWarpedEvent;
import superstartrek.client.activities.navigation.EnterpriseWarpedHandler;
import superstartrek.client.activities.navigation.ThingMovedEvent;
import superstartrek.client.activities.navigation.ThingMovedHandler;
import superstartrek.client.activities.sector.contextmenu.SectorContextMenuPresenter;
import superstartrek.client.activities.sector.contextmenu.SectorSelectedEvent;
import superstartrek.client.activities.sector.contextmenu.SectorSelectedHandler;
import superstartrek.client.control.AfterTurnStartedEvent;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.GameStartedEvent;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;

public class QuadrantScannerPresenter extends BasePresenter<QuadrantScannerActivity>
		implements SectorSelectedHandler, GamePhaseHandler, ThingMovedHandler, EnterpriseWarpedHandler, FireHandler,
		EnterpriseRepairedHandler, KlingonUncloakedHandler, KlingonDestroyedHandler {

	SectorContextMenuPresenter sectorMenuPresenter;

	public void onSectorSelected(int x, int y, int screenX, int screenY) {
		application.events.fireEvent(new SectorSelectedEvent(Location.location(x, y),
				application.starMap.enterprise.getQuadrant(), screenX, screenY));
	}

	public QuadrantScannerPresenter(Application application, SectorContextMenuPresenter sectorMenuPresenter) {
		super(application);
		application.events.addHandler(SectorSelectedEvent.TYPE, this);
		application.events.addHandler(GameStartedEvent.TYPE, this);
		application.events.addHandler(ThingMovedEvent.TYPE, this);
		application.events.addHandler(EnterpriseWarpedEvent.TYPE, this);
		application.events.addHandler(FireEvent.TYPE, this);
		application.events.addHandler(EnterpriseRepairedEvent.TYPE, this);
		application.events.addHandler(KlingonDestroyedEvent.TYPE, this);
		application.events.addHandler(KlingonUncloakedEvent.TYPE, this);
		application.events.addHandler(AfterTurnStartedEvent.TYPE, this);
		this.sectorMenuPresenter = sectorMenuPresenter;
	}

	@Override
	public void onSectorSelected(SectorSelectedEvent event) {
		((IQuadrantScannerView) getView()).deselectSectors();
		((IQuadrantScannerView) getView()).selectSector(event.sector.getX(), event.sector.getY());
	}

	void updateSector(Thing thing) {
		String content = thing.getSymbol();
		String css = thing.getCss();
		if (thing instanceof Vessel) {
			Vessel vessel = (Vessel) thing;
			double status = vessel.getShields().health();
			css += " " + CSS.damageClass(status);
		}
		((IQuadrantScannerView) view).updateSector(thing.getLocation().getX(), thing.getLocation().getY(), content,
				css);
	}

	void clearSector(int x, int y) {
		((IQuadrantScannerView) getView()).updateSector(x, y, "", "");
	}
	
	void markSectorAsNavigationTarget(int x, int y) {
		((IQuadrantScannerView) view).addCssToCell(x, y, "navigation-target");
	}

	void updateSector(Quadrant q, int x, int y) {
		StarMap starMap = getApplication().starMap;
		Thing thing = starMap.findThingAt(q, x, y);
		if (thing != null)
			updateSector(thing);
		else
			clearSector(x, y);
	}
	
	void mark(Thing thing, Thing[][] array) {
		Location location = thing.getLocation();
		array[location.getX()][location.getY()] = thing;
	}

	void updateScreen() {
		StarMap starMap = getApplication().starMap;
		Quadrant q = starMap.enterprise.getQuadrant();
		if (q == null)
			throw new RuntimeException("q is null");
		// we could just erase all sectors first and paint things over it, but that would increase DOM interactions.
		// this approach (render into an array first, paint each sector only once) minimises DOM interactions.
		Thing[][] arr = new Thing[8][8];
		for (Thing t:q.getKlingons())
			mark(t,arr);
		for (Thing t:q.getStars())
			mark(t,arr);
		if (q.getStarBase() != null)
			mark(q.getStarBase(), arr);
		mark(starMap.enterprise, arr);
		for (int x = 0; x < 8; x++) 
		for (int y = 0; y < 8; y++) {
				Thing t = arr[x][y];
				if (t!=null)
					updateSector(t);
				else
					clearSector(x, y);
			}
	}

	@Override
	public void onGameStarted(GameStartedEvent evt) {
		updateScreen();
	}

	@Override
	public void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
		// TODO: this all assumes that qTo is the currently visible quadrant. Validate
		// respectively.
		clearSector(lFrom.getX(), lFrom.getY());
		updateSector(thing);
	}

	@Override
	public void onEnterpriseWarped(Enterprise enterprise, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
		updateScreen();
	}

	@Override
	public void onEnterpriseRepaired(Enterprise enterprise) {
		updateSector(enterprise.getQuadrant(), enterprise.getLocation().getX(), enterprise.getLocation().getY());
	}

	@Override
	public void klingonUncloaked(Klingon klingon) {
		updateSector(klingon);
	}

	@Override
	public void klingonDestroyed(Klingon klingon) {
		clearSector(klingon.getLocation().getX(), klingon.getLocation().getY());
	}

	@Override
	public void afterFire(FireEvent evt) {
		// target might have been destroyed (so not on map anymore), that's why we don't
		// call updateSector(target)
		Thing target = evt.target;
		if (target != null)
			updateSector(target.getQuadrant(), target.getLocation().getX(), target.getLocation().getY());
	}
	
	public void clearAllNavigationTargets() {
		IQuadrantScannerView view = (IQuadrantScannerView)getView();
		for (int y=0;y<8;y++)
			for (int x=0;x<8;x++)
				view.removeCssFromCell(x,y,"navigation-target");
	}
	
	@Override
	public void afterTurnStarted(AfterTurnStartedEvent evt) {
		List<Location> sectors = application.starMap.enterprise.getReachableSectors();
		clearAllNavigationTargets();
		for (Location l:sectors)
			markSectorAsNavigationTarget(l.getX(), l.getY());
	}
	
}
