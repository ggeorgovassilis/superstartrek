package superstartrek.client.activities.computer.quadrantscanner;

import java.util.ArrayList;
import java.util.List;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.CSS;
import superstartrek.client.activities.combat.FireEvent;
import superstartrek.client.activities.combat.FireHandler;
import superstartrek.client.activities.computer.srs.MapCellRenderer;
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
		application.events.fireEvent(new SectorSelectedEvent(Location.location(x,y), application.starMap.enterprise.getQuadrant(), screenX, screenY));
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

	protected void updateSector(Quadrant q, int x, int y, Thing thing){
		//empty table cells need an &nbsp; to keep height stable, otherwise they will "pump" when content changes
		// and relayout the entire screen which is slow on mobile devices
		String content = MapCellRenderer.nbsp;
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
	
	protected void updateSector(Quadrant q, int x, int y) {
		StarMap starMap = getApplication().starMap;
		Thing thing = starMap.findThingAt(q, x, y);
		updateSector(q, x, y, thing);
	}

	protected void updateScreen() {
		StarMap starMap = getApplication().starMap;
		Quadrant q = starMap.enterprise.getQuadrant();
		if (q == null)
			throw new RuntimeException("q is null");
		// since findThingAt is slow it cannot be used for the full screen update
		// that's why we iterate over the things in the quadrant instead over the sectors
		
		for (int y = 0; y < 8; y++)
			for (int x = 0; x < 8; x++) {
				((IQuadrantScannerView) view).updateSector(x, y, MapCellRenderer.nbsp, "");
			}
		List<Thing> things = new ArrayList<>();
		things.addAll(q.getKlingons());
		things.addAll(q.getStars());
		if (q.getStarBase()!=null)
			things.add(q.getStarBase());
		things.add(starMap.enterprise);
		for (Thing thing:things) {
			updateSector(q, thing.getLocation().getX(), thing.getLocation().getY(), thing);
		}
	}

	@Override
	public void onGameStared(GameStartedEvent evt) {
		updateScreen();
	}

	@Override
	public void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
		//TODO: this all assumes that qTo is the currently visible quadrant. Validate respectively.
		updateSector(qFrom, lFrom.getX(), lFrom.getY());
		updateSector(qTo, lTo.getX(), lTo.getY());
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
	}
	
	@Override
	public void onFire(Vessel actor, Thing target, String weapon, double damage) {
	}


	@Override
	public void afterFire(Vessel actor, Thing target, String weapon, double damage) {
		updateSector(target.getQuadrant(), target.getLocation().getX(), target.getLocation().getY());
	}


}
