package superstartrek.client.activities.computer.srs;

import superstartrek.client.activities.BasePresenter;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.QuadrantActivationHandler;
import superstartrek.client.eventbus.Events;
import superstartrek.client.space.Constants;
import superstartrek.client.space.Quadrant;
import superstartrek.client.space.StarMap;
import superstartrek.client.utils.Maps;
import superstartrek.client.vessels.CombatHandler;
import superstartrek.client.vessels.Vessel;

public class SRSPresenter extends BasePresenter<SRSView>
		implements GamePhaseHandler, CombatHandler, QuadrantActivationHandler{

	public SRSPresenter() {
		addHandler(Events.GAME_STARTED);
		addHandler(Events.QUADRANT_ACTIVATED);
		addHandler(Events.KLINGON_DESTROYED);
	}

	public void updateRadar() {
		StarMap map = getApplication().starMap;
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
		StarMap map = getApplication().starMap;
		Quadrant q = getActiveQuadrant();
		Maps.renderCell(1, 1, map, q, "", view);
	}

	public void quadrantWasClicked(int dx, int dy) {
		StarMap map = getApplication().starMap;
		Quadrant q = getActiveQuadrant();
		int x = q.x + dx;
		int y = q.y + dy;
		if (map.isOnMap(x, y))
			map.enterprise.warpTo(map.getQuadrant(x, y), null);
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
