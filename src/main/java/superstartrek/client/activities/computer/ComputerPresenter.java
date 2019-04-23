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

public class ComputerPresenter extends BasePresenter<IComputerView>
		implements ComputerHandler, GamePhaseHandler, FireHandler, KlingonDestroyedHandler, ValueChangeHandler<String> {

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
		updateStarDate();
		getView().show();
	}

	public void updateStarDate() {
		getView().showStarDate("" + application.starMap.getStarDate());
	}

	@Override
	public void hideScreen() {
		getView().hide();
	}

	public void onDockInStarbaseButtonClicked() {
		StarMap map = application.starMap;
		Enterprise enterprise = map.enterprise;
		Quadrant q = enterprise.getQuadrant();
		boolean inRange = StarMap.within_distance(enterprise, q.getStarBase(), 1.1);
		if (!inRange) {
			Location loc = application.starMap.findFreeSpotAround(q, q.getStarBase().getLocation(), 2);
			if (loc == null) {
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
		boolean visible = starBase != null
				&& (q.getKlingons().isEmpty() || StarMap.within_distance(enterprise, starBase, 2));
		getView().setDockInStarbaseButtonVisibility(visible);
	}

	public void updateRepairButton() {
		Enterprise enterprise = application.starMap.enterprise;
		getView().setRepairButtonVisibility(enterprise.canRepairProvisionally());
	}

	public void updateStatusButton() {
		Enterprise enterprise = application.starMap.enterprise;
		String cssImpulse = CSS.damageClass(enterprise.getImpulse().health());
		String cssTactical = CSS.damageClass(enterprise.getAutoAim().health());
		String cssPhasers = CSS.damageClass(enterprise.getPhasers().health());
		String cssTorpedos = CSS.damageClass(enterprise.getTorpedos().health());

		getView().updateShortStatus(cssImpulse, cssTactical, cssPhasers, cssTorpedos);
	}

	@Override
	public void onTurnStarted(TurnStartedEvent evt) {
		updateStarDate();
		updateDockInStarbaseButton();
		updateShieldsView();
		updateStatusButton();
		updateRepairButton();
		updateQuadrantHeader();
		updateAntimatter();
	}

	public void updateAntimatter() {
		Setting antimatter = application.starMap.enterprise.getAntimatter();
		getView().updateAntimatter((int) antimatter.getValue(), (int) antimatter.getMaximum());
	}

	public void updateQuadrantHeader() {
		String alert = "";
		Quadrant q = application.starMap.enterprise.getQuadrant();
		Enterprise e = application.starMap.enterprise;
		Location el = e.getLocation();
		if (!q.getKlingons().isEmpty()) {
			double minDistance = 3 * 3;
			for (Klingon k : q.getKlingons())
				minDistance = Math.min(minDistance,
						StarMap.distance_squared(el.getX(), el.getY(), k.getLocation().getX(), k.getLocation().getY()));
			alert = minDistance < 9 ? "red-alert" : "yellow-alert";
		}

		getView().setQuadrantName(q.getName(), alert);
	}

	public void updateShieldsView() {
		Enterprise enterprise = application.starMap.enterprise;
		Setting shields = enterprise.getShields();
		getView().updateShields((int) shields.getValue(), (int) shields.getCurrentUpperBound(), (int) shields.getMaximum());
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
