package superstartrek.client.activities.computer.srs;

import com.google.gwt.core.shared.GWT;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.combat.FireEvent;
import superstartrek.client.activities.combat.FireHandler;
import superstartrek.client.activities.computer.TurnStartedEvent;
import superstartrek.client.activities.computer.TurnStartedHandler;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.KlingonDestroyedEvent;
import superstartrek.client.activities.klingons.KlingonDestroyedHandler;
import superstartrek.client.activities.loading.GameStartedEvent;
import superstartrek.client.activities.loading.GameStartedHandler;
import superstartrek.client.activities.navigation.EnterpriseWarpedEvent;
import superstartrek.client.activities.navigation.EnterpriseWarpedHandler;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;
import superstartrek.client.utils.Maps;

public class SRSPresenter extends BasePresenter<SRSActivity> implements GameStartedHandler, EnterpriseWarpedHandler, KlingonDestroyedHandler {

	public SRSPresenter(Application application) {
		super(application);
		application.events.addHandler(GameStartedEvent.TYPE, this);
		application.events.addHandler(EnterpriseWarpedEvent.TYPE, this);
		application.events.addHandler(KlingonDestroyedEvent.TYPE, this);
	}

	public void updateRadar() {
		ISRSView view = (ISRSView) getView();
		StarMap map = application.starMap;
		Quadrant q0 = map.enterprise.getQuadrant();
		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 3; x++) {
				int qx = q0.getX() + x-1;
				int qy = q0.getY() + y-1;
				if (qx >= 0 && qy >= 0 && qx < 8 && qy < 8) {
					Quadrant q = map.getQuadrant(qx, qy);
					Maps.renderCell(x, y, map, q, view);
				}
				else 
					Maps.renderCell(x, y, map, null, view);
			}
	}
	
	public void quadrantWasClicked(int dx, int dy) {
		StarMap map = application.starMap;
		Quadrant q = application.starMap.enterprise.getQuadrant();
		int x = q.getX()+dx;
		int y = q.getY()+dy;
		if (map.isOnMap(x, y))
			map.enterprise.warpTo(map.getQuadrant(x, y));
	}

	@Override
	public void onEnterpriseWarped(Enterprise enterprise, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
		updateRadar();
	}

	@Override
	public void klingonDestroyed(Klingon klingon) {
		updateRadar();
	}

	@Override
	public void onGameStared(GameStartedEvent evt) {
		updateRadar();
	}

}
