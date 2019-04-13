package superstartrek.client.activities.computer;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.CSS;
import superstartrek.client.activities.combat.FireHandler;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.KlingonDestroyedHandler;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.TurnStartedEvent;
import superstartrek.client.control.YieldTurnEvent;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Setting;
import superstartrek.client.model.StarBase;
import superstartrek.client.model.StarMap;

public class ComputerPresenter extends BasePresenter<ComputerActivity> implements ComputerHandler, GamePhaseHandler, FireHandler, KlingonDestroyedHandler, ValueChangeHandler<String>{

	public ComputerPresenter(Application application) {
		super(application);
		application.addHistoryListener(this);
		application.events.addHandler(ComputerEvent.TYPE, this);
		application.events.addHandler(TurnStartedEvent.TYPE, this);
		application.events.addHandler(KlingonDestroyedEvent.TYPE, this);
	}
	
	public void onSkipButtonClicked() {
		application.events.fireEvent(new YieldTurnEvent());
	}

	@Override
	public void showScreen() {
		((IComputerView)getView()).showStarDate(""+application.starMap.getStarDate());
		getView().show();
	}

	@Override
	public void hideScreen() {
		getView().hide();
	}
	
	public void onDockInStarbaseButtonClicked() {
		StarMap map = application.starMap;
		Enterprise enterprise = map.enterprise;
		Quadrant q = enterprise.getQuadrant();
		double distance = StarMap.distance(enterprise, q.getStarBase());
		if (distance>1) {
			Location loc = application.starMap.findFreeSpotAround(q, q.getStarBase().getLocation(),1);
			if (loc==null) {
				application.message("No space around starbase");
				return;
			}
			enterprise._navigateTo(loc);
		}
		enterprise.dockAtStarbase(q.getStarBase());
	}
	
	public void updateDockInStarbaseButton() {
		Enterprise enterprise = application.starMap.enterprise;
		Quadrant q = enterprise.getQuadrant();
		StarBase starBase = q.getStarBase();
		boolean visible = starBase!=null && (q.getKlingons().isEmpty() || StarMap.distance(enterprise, starBase)<2);
		((IComputerView)getView()).setDockInStarbaseButtonVisibility(visible);
	}
	
	public void updateRepairButton() {
		Enterprise enterprise = application.starMap.enterprise;
		((IComputerView)getView()).setRepairButtonVisibility(enterprise.canRepairProvisionally());
	}
	
	public void updateStatusButton() {
		Enterprise enterprise = application.starMap.enterprise;
		String cssImpulse = CSS.damageClass(enterprise.getImpulse().health());
		String cssTactical= CSS.damageClass(enterprise.getAutoAim().health());
		String cssPhasers = CSS.damageClass(enterprise.getPhasers().health());
		String cssTorpedos= CSS.damageClass(enterprise.getTorpedos().health());
		
		((IComputerView)getView()).updateShortStatus(cssImpulse, cssTactical, cssPhasers, cssTorpedos);
	}

	@Override
	public void onTurnStarted(TurnStartedEvent evt) {
		IComputerView view = (IComputerView)getView();
		view.showStarDate(""+application.starMap.getStarDate());
		updateDockInStarbaseButton();
		updateShieldsView();
		updateStatusButton();
		updateRepairButton();
		updateQuadrantHeader();
	}
	
	public void updateQuadrantHeader() {
		String alert = "";
		Quadrant q = application.starMap.enterprise.getQuadrant();
		Enterprise e = application.starMap.enterprise;
		if (!q.getKlingons().isEmpty()) {
			double minDistance = 3;
			for (Klingon k:q.getKlingons())
				minDistance = Math.min(minDistance, StarMap.distance(e, k));
			alert = minDistance<3?"red-alert":"yellow-alert";
		}
		
		IComputerView view = (IComputerView)getView();
		view.setQuadrantName(q.getName(), alert);
	}

	
	public void updateShieldsView() {
		Enterprise enterprise = application.starMap.enterprise;
		Setting shields = enterprise.getShields();
		IComputerView view = (IComputerView)getView();
		view.updateShields(shields);
	}

	public void onToggleShieldsButtonClicked() {
		Enterprise enterprise = application.starMap.enterprise;
		Setting shields = enterprise.getShields();
		double value = enterprise.getShields().getValue();
		if (value == shields.getCurrentUpperBound())
			value = 0;
		else
			value = Math.min(value+shields.getMaximum()/4, shields.getCurrentUpperBound());
		shields.setValue(value);
		updateShieldsView();
	}
	
	public void onRepairButtonClicked() {
		Enterprise enterprise = application.starMap.enterprise;
		enterprise.repairProvisionally();
		updateStatusButton();
	}

	@Override
	public void afterFire(FireEvent evt) {
		if (evt.target == application.starMap.enterprise) {
			updateShieldsView();
		}
	}

	@Override
	public void klingonDestroyed(Klingon klingon) {
		updateQuadrantHeader();
		updateDockInStarbaseButton();
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		if ("computer".equals(event.getValue()))
			showScreen();
		else
			hideScreen();
	}
}
