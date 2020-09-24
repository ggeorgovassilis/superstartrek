package superstartrek.client.activities.computer.srs;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.combat.CombatHandler;
import superstartrek.client.bus.Commands;
import superstartrek.client.bus.Events;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.QuadrantActivationHandler;
import superstartrek.client.model.Constants;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Vessel;
import superstartrek.client.utils.Maps;

public class SRSPresenter extends BasePresenter<ISRSView>
		implements GamePhaseHandler, CombatHandler, QuadrantActivationHandler{

	public SRSPresenter(Application application) {
		super(application);
		addHandler(Events.GAME_STARTED, this);
		addHandler(Events.QUADRANT_ACTIVATED, this);
		addHandler(Events.KLINGON_DESTROYED, this);
	}

	public void updateRadar() {
		StarMap map = application.starMap;
		Quadrant q0 = getActiveQuadrant();
		for (int y = 0; y < 3; y++) {
			int qy = q0.y + y - 1;
			for (int x = 0; x < 3; x++) {
				int qx = q0.x + x - 1;
				if (qx >= 0 && qy >= 0 && qx < Constants.SECTORS_EDGE && qy < Constants.SECTORS_EDGE) {
					Quadrant q = map.getQuadrant(qx, qy);
					Maps.renderCell(x, y, map, q, "", view);
				} else
					Maps.renderCell(x, y, map, null, "", view);
			}
		}
	}
	
	public void updateCenterQuadrant() {
		StarMap map = application.starMap;
		Quadrant q = getActiveQuadrant();
		Maps.renderCell(1, 1, map, q, "", view);
	}

	public void quadrantWasClicked(int dx, int dy) {
		StarMap map = application.starMap;
		Quadrant q = getActiveQuadrant();
		int x = q.x + dx;
		int y = q.y + dy;
		if (map.isOnMap(x, y))
			map.enterprise.warpTo(map.getQuadrant(x, y), null);
	}

	public void onAppMenuButtonClicked() {
		fireEvent(Commands.APP_MENU_SHOW, (h)->h.showMenu());
	}

	@Override
	public void onActiveQuadrantChanged(Quadrant oldQuadrant, Quadrant newQuadrant) {
		updateRadar();
	}
	
	@Override
	public void onVesselDestroyed(Vessel vessel) {
		updateCenterQuadrant();
	}

	@Override
	public void onGameStarted(StarMap map) {
		updateRadar();
	}

}
