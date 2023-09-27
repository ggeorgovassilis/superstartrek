package superstartrek.client.activities.lrs;

import superstartrek.client.activities.ActivityChangedHandler;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.eventbus.Events;
import superstartrek.client.space.Constants;
import superstartrek.client.space.Quadrant;
import superstartrek.client.space.StarMap;
import superstartrek.client.utils.Maps;
import superstartrek.client.vessels.Enterprise;

public class LRSPresenter extends BasePresenter<LRSScreen> implements ActivityChangedHandler {

	public LRSPresenter() {
		addHandler(Events.ACTIVITY_CHANGED);
	}

	public void quadrantWasClicked(int x, int y) {
		StarMap starMap = getApplication().starMap;
		Quadrant qTo = starMap.getQuadrant(x, y);
		Enterprise enterprise = starMap.enterprise;
		if (enterprise.getQuadrant() == qTo)
			return;
		// there's a significant benefit in hiding LRS before going through the CPU
		// intensive
		// warping event cascade
		// invoked only before successful warp
		if (enterprise.warpTo(qTo, () -> view.hide()))
			getApplication().browserAPI.postHistoryChange("computer");
	}

	protected void updateQuadrant(int x, int y, boolean isReachable) {
		StarMap map = getApplication().starMap;
		Quadrant q = map.getQuadrant(x, y);
		Maps.renderCell(x, y, map, q, isReachable ? "navigation-target " : "", view);
	}

	protected void updateEnterpriseLocation() {
		Enterprise enterprise = getEnterprise();
		Quadrant q = enterprise.getQuadrant();
		view.addCss(q.x, q.y, "has-enterprise");
	}

	public void updateLrsView() {
		StarMap starMap = getApplication().starMap;
		Enterprise enterprise = starMap.enterprise;
		Quadrant qEnterprise = enterprise.getQuadrant();
		for (int y = 0; y < Constants.SECTORS_EDGE; y++)
			for (int x = 0; x < Constants.SECTORS_EDGE; x++) {
				double requiredEnergy = enterprise.computeConsumptionForWarp(qEnterprise, starMap.getQuadrant(x, y));
				boolean isReachable = enterprise.getWarpDrive().isOperational() && (requiredEnergy <= enterprise.getReactor().getValue());
				updateQuadrant(x, y, isReachable);
			}
		updateEnterpriseLocation();
	}

	public void showLrs() {
		updateLrsView();
		view.show();
	}

	@Override
	public void onActivityChanged(String activity) {
		if ("longrangescan".equals(activity))
			showLrs();
		else
			view.hide();
	}
	
}
