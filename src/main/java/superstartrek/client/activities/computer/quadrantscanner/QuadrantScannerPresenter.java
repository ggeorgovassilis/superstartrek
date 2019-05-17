package superstartrek.client.activities.computer.quadrantscanner;

import java.util.List;

import com.google.gwt.event.dom.client.KeyCodes;
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
import superstartrek.client.activities.pwa.Callback;
import superstartrek.client.activities.sector.contextmenu.SectorContextMenuPresenter;
import superstartrek.client.activities.sector.contextmenu.SectorSelectedHandler;
import superstartrek.client.bus.Events;
import superstartrek.client.control.AfterTurnStartedEvent;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.GameStartedEvent;
import superstartrek.client.control.KeyPressedEventHandler;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;
import superstartrek.client.activities.sector.contextmenu.ContextMenuHideHandler;

public class QuadrantScannerPresenter extends BasePresenter<IQuadrantScannerView>
		implements SectorSelectedHandler, GamePhaseHandler, ThingMovedHandler, EnterpriseWarpedHandler, FireHandler,
		EnterpriseRepairedHandler, KlingonCloakingHandler, KlingonDestroyedHandler, KeyPressedEventHandler {

	SectorContextMenuPresenter sectorMenuPresenter;
	Location selectedSector = Location.location(0, 0);

	public void onSectorSelected(int x, int y, int screenX, int screenY) {
		Location location = Location.location(x, y);
		Quadrant quadrant = application.starMap.enterprise.getQuadrant();
		application.bus.invoke(Events.SECTOR_SELECTED, (Callback<SectorSelectedHandler>)(h)->h.onSectorSelected(location, quadrant, screenX, screenY));
	}

	public QuadrantScannerPresenter(Application application, SectorContextMenuPresenter sectorMenuPresenter) {
		super(application);
		this.sectorMenuPresenter = sectorMenuPresenter;
		addHandler(Events.SECTOR_SELECTED, this);
		addHandler(GameStartedEvent.TYPE, this);
		addHandler(ThingMovedEvent.TYPE, this);
		application.bus.register(Events.AFTER_ENTERPRISE_WARPED, this);
		application.bus.register(Events.AFTER_FIRE, this);
		addHandler(Events.ENTERPRISE_REPAIRED, this);
		addHandler(Events.KLINGON_DESTROYED, this);
		application.bus.register(Events.KLINGON_CLOAKED, this);
		application.bus.register(Events.KLINGON_UNCLOAKED, this);
		addHandler(AfterTurnStartedEvent.TYPE, this);
		addHandler(KeyPressedEvent.TYPE, this);
	}

	@Override
	public void onSectorSelected(Location sector, Quadrant quadrant, int screenX, int screen) {
		view.deselectSectors();
		selectedSector = sector;
		view.selectSector(selectedSector.getX(), selectedSector.getY());
	}

	void updateSector(Thing thing) {
		String content = null;
		String css = null;
		if (thing.isVisible()) {
			content = thing.getSymbol();
			css = thing.getCss();
			if (Vessel.is(thing)) {
				Vessel vessel = thing.as();
				css += " " + CSS.damageClass(vessel.getShields());
				if (!vessel.getImpulse().isEnabled())
					css += " impulse-disabled";
				if (Klingon.is(thing) && (!Klingon.as(thing).getDisruptor().isEnabled())) {
					css += " disruptor-disabled";
				}
			}
		} else {
			content = "";
			css = "";
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
		Thing thing = q.findThingAt(x, y);
		if (thing == null)
			clearSector(x, y);
		else
			updateSector(thing);
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
		for (int x = 0; x < 8; x++)
			for (int y = 0; y < 8; y++) {
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
	public void onKlingonDestroyed(Klingon klingon) {
		clearSector(klingon.getLocation().getX(), klingon.getLocation().getY());
	}

	@Override
	public void afterFire(Quadrant quadrant, Vessel actor, Thing target, String weapon, double damage, boolean wasAutoFire) {
		// target might have been destroyed (so not on map anymore) and thus null
		if (target != null)
			updateSector(quadrant, target.getLocation().getX(), target.getLocation().getY());
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

	@Override
	public void onKeyPressed(KeyPressedEvent event) {
		if (!view.isVisible())
			return;
		Location newSector = null;
		switch (event.code) {
		case KeyCodes.KEY_LEFT:
			newSector = Location.location(Math.max(0, selectedSector.getX() - 1), selectedSector.getY());
			break;
		case KeyCodes.KEY_RIGHT:
			newSector = Location.location(Math.min(7, selectedSector.getX() + 1), selectedSector.getY());
			break;
		case KeyCodes.KEY_UP:
			newSector = Location.location(selectedSector.getX(), Math.max(0, selectedSector.getY() - 1));
			break;
		case KeyCodes.KEY_DOWN:
			newSector = Location.location(selectedSector.getX(), Math.min(7, selectedSector.getY() + 1));
			break;
		}

		if (event.code == 0)
			switch (event.charCode) {
			case 'M':
			case 'm':
				int dx = view.getHorizontalOffsetOfSector(selectedSector.getX(), selectedSector.getY());
				int dy = view.getVerticalOffsetOfSector(selectedSector.getX(), selectedSector.getY());
				Quadrant quadrant = application.starMap.enterprise.getQuadrant();
				application.bus.invoke(Events.SECTOR_SELECTED, (Callback<SectorSelectedHandler>)(h)->h.onSectorSelected(selectedSector, quadrant, dx, dy));
				break;
			}
		if (newSector != null) {
			selectedSector = newSector;
			view.deselectSectors();
			view.selectSector(selectedSector.getX(), selectedSector.getY());
			application.bus.invoke(Events.CONTEXT_MENU_HIDE, (Callback<ContextMenuHideHandler>)(h)->h.onMenuHide());
		}
	}

}
