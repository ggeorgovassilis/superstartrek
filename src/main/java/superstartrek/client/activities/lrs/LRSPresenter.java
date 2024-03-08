package superstartrek.client.activities.lrs;

import com.google.gwt.touch.client.Point;

import superstartrek.client.activities.ActivityChangedHandler;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.eventbus.Events;
import superstartrek.client.space.Constants;
import superstartrek.client.space.Quadrant;
import superstartrek.client.space.StarMap;
import superstartrek.client.uihandler.InteractionHandler;
import superstartrek.client.uihandler.UiHandler;
import superstartrek.client.utils.Maps;
import superstartrek.client.vessels.Enterprise;

public class LRSPresenter extends BasePresenter<LRSScreen> implements ActivityChangedHandler, InteractionHandler {

	public LRSPresenter() {
		addHandler(Events.ACTIVITY_CHANGED);
		addHandler(Events.INTERACTION);
	}

	public void quadrantWasClicked(int x, int y) {
		StarMap starMap = getStarMap();
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

	void updateQuadrant(Quadrant quadrant, boolean isReachable) {
		Maps.renderCell(quadrant.x, quadrant.y, getStarMap(), quadrant, isReachable ? "navigation-target " : "", view);
	}

	void updateEnterpriseLocation() {
		Enterprise enterprise = getEnterprise();
		Quadrant q = enterprise.getQuadrant();
		view.addCss(q.x, q.y, "has-enterprise");
	}

	void updateLrsView() {
		StarMap starMap = getStarMap();
		Enterprise enterprise = getEnterprise();
		Quadrant qEnterprise = enterprise.getQuadrant();
		boolean doesWarpdriveWork = enterprise.getWarpDrive().isOperational();
		double reactor = enterprise.getReactor().getValue();
		for (int y = 0; y < Constants.SECTORS_EDGE; y++)
			for (int x = 0; x < Constants.SECTORS_EDGE; x++) {
				Quadrant quadrant = starMap.getQuadrant(x, y);
				double requiredEnergy = enterprise.computeConsumptionForWarp(qEnterprise, quadrant);
				boolean isReachable = doesWarpdriveWork && (requiredEnergy <= reactor);
				updateQuadrant(quadrant, isReachable);
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

	@Override
	public void onUiInteraction(String tag) {
		if (!tag.startsWith("q_"))
			return;
		Point xy = UiHandler.parseCoordinatesFromTag(tag);
		quadrantWasClicked((int)xy.getX(), (int)xy.getY());
	}

}
