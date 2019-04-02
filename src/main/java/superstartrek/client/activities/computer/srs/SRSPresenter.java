package superstartrek.client.activities.computer.srs;

import com.google.gwt.core.client.GWT;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.KlingonDestroyedHandler;
import superstartrek.client.activities.navigation.EnterpriseWarpedEvent;
import superstartrek.client.activities.navigation.EnterpriseWarpedHandler;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.GameStartedEvent;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;
import superstartrek.client.utils.Maps;

public class SRSPresenter extends BasePresenter<SRSActivity> implements GamePhaseHandler, EnterpriseWarpedHandler, KlingonDestroyedHandler {

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
			map.enterprise.warpTo(map.getQuadrant(x, y), null);
	}

	@Override
	public void onEnterpriseWarped(Enterprise enterprise, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
		updateRadar();
	}

	@Override
	public void klingonDestroyed(Klingon klingon) {
		//actually only the center cell on the SRS map needs to be updated, but this event is rare and drawing the radar not expensive
		updateRadar();
	}

	@Override
	public void onGameStarted(GameStartedEvent evt) {
		updateRadar();
	}

}
