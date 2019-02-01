package superstartrek.client.activities.lrs;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.navigation.EnterpriseWarpedEvent;
import superstartrek.client.activities.navigation.EnterpriseWarpedHandler;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;

public class LRSPresenter extends BasePresenter<LRSActivity> implements LRSHandler, EnterpriseWarpedHandler {

	public LRSPresenter(Application application) {
		super(application);
		application.events.addHandler(LRSEvent.TYPE, this);
		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				if ("longrangescan".equals(event.getValue()))
					lrsShown();
				else
					lrsHidden();
			}
		});
		application.events.addHandler(EnterpriseWarpedEvent.TYPE, this);
	}

	public void quadrantWasClicked(int x, int y) {
		Quadrant qTo = application.starMap.getQuadrant(x, y);
		Enterprise enterprise = application.starMap.enterprise;
		enterprise.warpTo(qTo);
		History.newItem("computer");
	}
	
	protected void updateQuadrant(int x, int y) {
		StarMap map = application.starMap;
		Quadrant q = map.getQuadrant(x, y);
		String text = "";
		String css = q.isExplored()?"explored":"";
		if (q.isExplored() && !q.getKlingons().isEmpty()) {
			css += " has-klingons";
			text += "K";
		} else
			text += " ";
		if (!q.getStarBases().isEmpty()) {
			css += " has-starbase";
			text += "!";
		} else
			text += " ";
		text += q.getStars().size();
		((LRSScreen) getView()).updateQuadrant(x, y, text, css);
	}
	
	protected void updateEnterpriseLocation() {
		Enterprise enterprise = application.starMap.enterprise;
		Quadrant q = enterprise.getQuadrant();
		((LRSScreen) getView()).addCss(q.getX(), q.getY(), "has-enterprise");
	}

	public void updateLrsView() {
		for (int y = 0; y < 8; y++)
			for (int x = 0; x < 8; x++) {
				updateQuadrant(x, y);
			}
		updateEnterpriseLocation();
	}

	@Override
	public void lrsShown() {
		updateLrsView();
		getView().show();
	}

	@Override
	public void lrsHidden() {
		getView().hide();
	}

	@Override
	public void quadrantSelected() {
	}

	@Override
	public void onEnterpriseWarped(Enterprise enterprise, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
		updateQuadrant(qFrom.getX(), qFrom.getY());
		updateQuadrant(qTo.getX(), qTo.getY());
		updateEnterpriseLocation();
	}

}
