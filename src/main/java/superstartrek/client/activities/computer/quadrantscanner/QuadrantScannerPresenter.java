package superstartrek.client.activities.computer.quadrantscanner;

import java.util.List;

import com.google.gwt.event.shared.EventBus;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.CSS;
import superstartrek.client.activities.combat.FireHandler;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.KlingonDestroyedHandler;
import superstartrek.client.activities.klingons.KlingonCloakingHandler;
import superstartrek.client.activities.navigation.EnterpriseRepairedHandler;
import superstartrek.client.activities.navigation.EnterpriseWarpedHandler;
import superstartrek.client.activities.navigation.ThingMovedHandler;
import superstartrek.client.activities.sector.contextmenu.SectorContextMenuPresenter;
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

public class QuadrantScannerPresenter extends BasePresenter<IQuadrantScannerView>
		implements SectorSelectedHandler, GamePhaseHandler, ThingMovedHandler, EnterpriseWarpedHandler, FireHandler,
		EnterpriseRepairedHandler, KlingonCloakingHandler, KlingonDestroyedHandler {

	SectorContextMenuPresenter sectorMenuPresenter;

	public void onSectorSelected(int x, int y, int screenX, int screenY) {
		application.events.fireEvent(new SectorSelectedEvent(Location.location(x, y),
				application.starMap.enterprise.getQuadrant(), screenX, screenY));
	}

	public QuadrantScannerPresenter(Application application, SectorContextMenuPresenter sectorMenuPresenter) {
		super(application);
		this.sectorMenuPresenter = sectorMenuPresenter;
		EventBus events = application.events;
		events.addHandler(SectorSelectedEvent.TYPE, this);
		events.addHandler(GameStartedEvent.TYPE, this);
		events.addHandler(ThingMovedEvent.TYPE, this);
		events.addHandler(EnterpriseWarpedEvent.TYPE, this);
		events.addHandler(FireEvent.TYPE, this);
		events.addHandler(EnterpriseRepairedEvent.TYPE, this);
		events.addHandler(KlingonDestroyedEvent.TYPE, this);
		events.addHandler(KlingonUncloakedEvent.TYPE, this);
		events.addHandler(KlingonCloakedEvent.TYPE, this);
		events.addHandler(AfterTurnStartedEvent.TYPE, this);
	}

	@Override
	public void onSectorSelected(SectorSelectedEvent event) {
		view.deselectSectors();
		view.selectSector(event.sector.getX(), event.sector.getY());
	}

	void updateSector(Thing thing) {
		String content = thing.getSymbol();
		String css = thing.getCss();
		if (!thing.isVisible()) {
			content = "";
			css = "";
		} else if (thing instanceof Vessel) {
			Vessel vessel = thing.as();
			double status = vessel.getShields().health();
			css += " " + CSS.damageClass(status);
			css += vessel.getImpulse().isEnabled() ? " " : " impulse-disabled";
			if (thing instanceof Klingon) {
				css += ((Klingon) thing).getDisruptor().isEnabled() ? "" : " disruptor-disabled";
			}
		}
		view.updateSector(thing.getLocation().getX(), thing.getLocation().getY(), content, css);
	}

	void clearSector(int x, int y) {
		view.updateSector(x, y, "", "");
	}

	void markSectorAsNavigationTarget(int x, int y) {
		view.addCssToCell(x, y, "navigation-target");
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
		// we could just erase all sectors first and paint things over it, but that
		// would increase DOM interactions.
		// this approach (render into an array first, paint each sector only once)
		// minimises DOM interactions.
		Thing[][] arr = new Thing[8][8];
		for (Thing t : starMap.getEverythingIn(q))
			mark(t, arr);
		for (int y = 0; y < 8; y++)
			for (int x = 0; x < 8; x++) {
				Thing t = arr[x][y];
				if (t != null)
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
		// can't just clear sector, because the "from" location may refer to a different
		// quadrant when eg. Enterprise warps
		updateSector(qTo, lFrom.getX(), lFrom.getY());
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
	public void klingonCloaked(Klingon klingon) {
		updateSector(klingon);
	}

	@Override
	public void klingonDestroyed(Klingon klingon) {
		clearSector(klingon.getLocation().getX(), klingon.getLocation().getY());
	}

	@Override
	public void afterFire(FireEvent evt) {
		// target might have been destroyed (so not on map anymore) and thus null
		Thing target = evt.target;
		if (target != null)
			updateSector(evt.quadrant, target.getLocation().getX(), target.getLocation().getY());
	}

	public void clearAllNavigationTargets() {
		for (int y = 0; y < 8; y++)
			for (int x = 0; x < 8; x++)
				view.removeCssFromCell(x, y, "navigation-target");
	}

	public void updateMapWithReachableSectors() {
		List<Location> sectors = application.starMap.enterprise.findReachableSectors();
		clearAllNavigationTargets();
		for (Location l : sectors)
			markSectorAsNavigationTarget(l.getX(), l.getY());
	}

	@Override
	public void afterTurnStarted(AfterTurnStartedEvent evt) {
		updateMapWithReachableSectors();
	}

}
