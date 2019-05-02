package superstartrek.client.activities.lrs;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;
import superstartrek.client.utils.Maps;

public class LRSPresenter extends BasePresenter<ILRSScreen> implements ValueChangeHandler<String> {

	public LRSPresenter(Application application) {
		super(application);
		application.browserAPI.addHistoryListener(this);
	}

	public void quadrantWasClicked(int x, int y) {
		Quadrant qTo = application.starMap.getQuadrant(x, y);
		Enterprise enterprise = application.starMap.enterprise;
		if (enterprise.getQuadrant() == qTo)
			return;
		// there's a significant benefit in hiding LRS before going through the CPU
		// intensive
		// warping event cascade
		// invoked only before successful warp
		if (enterprise.warpTo(qTo, ()->	view.hide()))
			application.browserAPI.postHistoryChange("computer");
	}

	protected void updateQuadrant(int x, int y) {
		StarMap map = application.starMap;
		Quadrant q = map.getQuadrant(x, y);
		Maps.renderCell(x, y, map, q, view);
	}

	protected void updateEnterpriseLocation() {
		Enterprise enterprise = application.starMap.enterprise;
		Quadrant q = enterprise.getQuadrant();
		view.addCss(q.getX(), q.getY(), "has-enterprise");
	}

	public void updateLrsView() {
		for (int y = 0; y < 8; y++)
			for (int x = 0; x < 8; x++)
				updateQuadrant(x, y);
		updateEnterpriseLocation();
		markReachableQuadrants();
	}
	
	public void markReachableQuadrants() {
		StarMap map = application.starMap;
		Enterprise enterprise = map.enterprise;
		Quadrant qEnterprise = enterprise.getQuadrant();
		for (int y = 0; y < 8; y++)
			for (int x = 0; x < 8; x++) {
				//TODO: reuse symmetry?
				double requiredEnergy = application.starMap.enterprise.computeConsumptionForWarp(qEnterprise, map.getQuadrant(x, y));
				if (requiredEnergy <= enterprise.getReactor().getValue())
					view.addCss(x, y, "navigation-target");
			}
	}

	public void showLrs() {
		//TODO: LRS shouldn't be visible if damaged
		updateLrsView();
		view.show();
		Quadrant loc = application.starMap.enterprise.getQuadrant();
		view.focusCell(loc.getX(), loc.getY());
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		if ("longrangescan".equals(event.getValue()))
			showLrs();
		else
			view.hide();
	}

}
