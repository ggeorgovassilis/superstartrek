package superstartrek.client.activities.computer.quadrantscanner;

import java.util.List;

import com.google.gwt.event.dom.client.KeyCodes;
import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.CSS;
import superstartrek.client.activities.combat.CombatHandler;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.KlingonCloakingHandler;
import superstartrek.client.activities.navigation.EnterpriseRepairedHandler;
import superstartrek.client.activities.navigation.NavigationHandler;
import superstartrek.client.activities.sector.contextmenu.SectorContextMenuPresenter;
import superstartrek.client.activities.sector.contextmenu.SectorSelectedHandler;
import static superstartrek.client.bus.Events.*;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.KeyPressedEventHandler;
import superstartrek.client.control.QuadrantActivationHandler;
import superstartrek.client.model.Constants;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;
import superstartrek.client.model.Weapon;

public class QuadrantScannerPresenter extends BasePresenter<IQuadrantScannerView>
		implements SectorSelectedHandler, GamePhaseHandler, NavigationHandler, CombatHandler, EnterpriseRepairedHandler,
		KlingonCloakingHandler, KeyPressedEventHandler, QuadrantActivationHandler {

	SectorContextMenuPresenter sectorMenuPresenter;
	Location selectedSector = Location.location(0, 0);

	public void onSectorSelected(int x, int y, int screenX, int screenY) {
		Location location = Location.location(x, y);
		Quadrant quadrant = application.starMap.enterprise.getQuadrant();
		// this is an event instead of directly calling #onSectorSelected(...) because
		// others fire this event to in order to update the view
		fireEvent(SECTOR_SELECTED, (h) -> h.onSectorSelected(location, quadrant, screenX, screenY));
	}

	public QuadrantScannerPresenter(Application application, SectorContextMenuPresenter sectorMenuPresenter) {
		super(application);
		this.sectorMenuPresenter = sectorMenuPresenter;
		addHandler(SECTOR_SELECTED, this);
		addHandler(GAME_STARTED, this);
		addHandler(THING_MOVED, this);
		addHandler(QUADRANT_ACTIVATED, this);
		addHandler(AFTER_FIRE, this);
		addHandler(ENTERPRISE_REPAIRED, this);
		addHandler(KLINGON_DESTROYED, this);
		addHandler(KLINGON_CLOAKED, this);
		addHandler(KLINGON_UNCLOAKED, this);
		addHandler(AFTER_TURN_STARTED, this);
		addHandler(KLINGON_TURN_STARTED, this);
		addHandler(KEY_PRESSED, this);
	}

	@Override
	public void onSectorSelected(Location sector, Quadrant quadrant, int screenX, int screen) {
		view.deselectSectors();
		selectedSector = sector;
		view.selectSector(selectedSector.x, selectedSector.y);
	}

	void updateSector(Thing thing) {
		String content = "";
		String css = "";
		if (thing.isVisible()) {
			content = thing.getSymbol();
			css = thing.getCss();
			if (Vessel.is(thing)) {
				Vessel vessel = thing.as();
				css += " " + CSS.damageClass(vessel.getShields());
				if (!vessel.getImpulse().isOperational())
					css += " impulse-disabled";
				if (Klingon.is(thing) && (!Klingon.as(thing).getDisruptor().isOperational())) {
					css += " disruptor-disabled";
				}
			}
		}
		view.updateSector(thing.getLocation().x, thing.getLocation().y, content, css);
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
		array[location.x][location.y] = thing;
	}

	void updateScreen() {
		StarMap starMap = getApplication().starMap;
		Quadrant q = starMap.enterprise.getQuadrant();
		// we could just erase all sectors first and paint things over it, but that
		// would increase DOM interactions.
		// this approach (render into an array first, paint each sector only once)
		// minimises DOM interactions.
		Thing[][] arr = new Thing[Constants.SECTORS_EDGE][Constants.SECTORS_EDGE];
		q.doWithThings(t->mark(t,arr));
		for (int x = 0; x < arr.length; x++)
			for (int y = 0; y < arr[x].length; y++) {
				Thing t = arr[x][y];
				if (t != null)
					updateSector(t);
				else
					clearSector(x, y);
			}
	}

	@Override
	public void onGameStarted(StarMap map) {
		updateScreen();
	}

	@Override
	public void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
		// can't just clear sector, because the "from" location may refer to a different
		// quadrant when eg. Enterprise warps
		updateSector(qTo, lFrom.x, lFrom.y);
		updateSector(thing);
	}

	@Override
	public void onActiveQuadrantChanged(Quadrant oldQuadrant, Quadrant newQuadrant) {
		updateScreen();
	}

	@Override
	public void onEnterpriseRepaired(Enterprise enterprise) {
		updateSector(enterprise.getQuadrant(), enterprise.getLocation().x, enterprise.getLocation().y);
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
	public void onVesselDestroyed(Vessel vessel) {
		clearSector(vessel.getLocation().x, vessel.getLocation().y);
	}

	@Override
	public void afterFire(Quadrant quadrant, Vessel actor, Thing target, Weapon weapon, double damage,
			boolean wasAutoFire) {
		// target might have been destroyed (so not on map anymore) and thus null
		if (target == null)
			return;
		updateSector(quadrant, target.getLocation().x, target.getLocation().y);
		switch (weapon) {
		case disruptor:
		case phaser:
			String colour = (actor == application.starMap.enterprise) ? "yellow" : "red";
			view.drawBeamBetween(actor.getLocation().x, actor.getLocation().y, target.getLocation().x,
					target.getLocation().y, colour);
			break;
		case torpedo:
			view.animateTorpedoFireBetween(actor.getLocation().x, actor.getLocation().y, target.getLocation().x,
					target.getLocation().y, (v)->{});
		}
	}

	public void clearNavigationTargets(List<Location> locations) {
		for (Location l : locations)
			view.removeCssFromCell(l.x, l.y, "navigation-target");
	}

	public void updateMapWithReachableSectors() {
		Enterprise enterprise = getStarMap().enterprise;
		clearNavigationTargets(enterprise.getLastReachableSectors());
		List<Location> sectors = enterprise.findReachableSectors();
		for (Location l : sectors)
			markSectorAsNavigationTarget(l.x, l.y);
	}

	@Override
	public void afterTurnStarted() {
		updateMapWithReachableSectors();
	}

	@Override
	public void onKeyPressed(int code) {
		if (!view.isVisible())
			return;
		Location newSector = null;
		switch (code) {
		case KeyCodes.KEY_LEFT:
			newSector = Location.location(Math.max(0, selectedSector.x - 1), selectedSector.y);
			break;
		case KeyCodes.KEY_RIGHT:
			newSector = Location.location(Math.min(Constants.SECTORS_EDGE - 1, selectedSector.x + 1),
					selectedSector.y);
			break;
		case KeyCodes.KEY_UP:
			newSector = Location.location(selectedSector.x, Math.max(0, selectedSector.y - 1));
			break;
		case KeyCodes.KEY_DOWN:
			newSector = Location.location(selectedSector.x,
					Math.min(Constants.SECTORS_EDGE - 1, selectedSector.y + 1));
			break;
		case 'M':
		case 'm':
			int dx = view.getHorizontalOffsetOfSector(selectedSector.x, selectedSector.y);
			int dy = view.getVerticalOffsetOfSector(selectedSector.x, selectedSector.y);
			Quadrant quadrant = application.starMap.enterprise.getQuadrant();
			fireEvent(SECTOR_SELECTED, (h) -> h.onSectorSelected(selectedSector, quadrant, dx, dy));
			break;
		}

		if (newSector != null) {
			selectedSector = newSector;
			view.deselectSectors();
			view.selectSector(selectedSector.x, selectedSector.y);
			fireEvent(CONTEXT_MENU_HIDDEN, (h) -> h.onMenuHidden());
		}
	}

	@Override
	public void onKlingonTurnStarted() {
		view.clearBeamMarks();
	}

}
