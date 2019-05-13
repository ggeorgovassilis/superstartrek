package superstartrek.client.activities.computer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.CSS;
import superstartrek.client.activities.combat.FireHandler;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.KlingonDestroyedHandler;
import superstartrek.client.activities.navigation.EnterpriseDamagedHandler;
import superstartrek.client.activities.navigation.EnterpriseRepairedHandler;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.GameRestartEvent;
import superstartrek.client.control.GameStartedEvent;
import superstartrek.client.control.ScoreKeeper;
import superstartrek.client.control.TurnStartedEvent;
import superstartrek.client.control.YieldTurnEvent;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Setting;
import superstartrek.client.model.StarBase;
import superstartrek.client.model.StarMap;

public class ComputerPresenter extends BasePresenter<IComputerScreen>
		implements ComputerHandler, GamePhaseHandler, FireHandler, KlingonDestroyedHandler, ValueChangeHandler<String>, EnterpriseDamagedHandler, EnterpriseRepairedHandler {

	ScoreKeeper scoreKeeper;
	boolean repairButtonDocksAtStarbase = false;
	
	public ComputerPresenter(Application application, ScoreKeeper scoreKeeper) {
		super(application);
		this.scoreKeeper = scoreKeeper;
		application.browserAPI.addHistoryListener(this);
		addHandler(ComputerEvent.TYPE, this);
		addHandler(TurnStartedEvent.TYPE, this);
		addHandler(KlingonDestroyedEvent.TYPE, this);
		addHandler(EnterpriseDamagedEvent.TYPE, this);
		addHandler(EnterpriseRepairedEvent.TYPE, this);
		addHandler(GameStartedEvent.TYPE, this);
	}

	public void onSkipButtonClicked() {
		application.events.fireEvent(new YieldTurnEvent());
	}

	@Override
	public void showScreen() {
		updateStarDate();
		view.show();
	}

	public void updateStarDate() {
		view.showStarDate("" + application.starMap.getStarDate());
	}

	public void updateScore() {
		view.showScore("" + scoreKeeper.getScore());
	}

	@Override
	public void hideScreen() {
		view.hide();
	}

	public void dockInStarbase() {
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
	
	public boolean canShowDockButton() {
		Enterprise enterprise = application.starMap.enterprise;
		Quadrant q = enterprise.getQuadrant();
		StarBase starBase = q.getStarBase();
		boolean visible = starBase != null
				&& (q.getKlingons().isEmpty() || StarMap.within_distance(enterprise, starBase, 2));
		return visible;
	}

	public void updateRepairButton() {
		Enterprise enterprise = application.starMap.enterprise;
		boolean canShowDockButton = canShowDockButton();
		boolean canShowRepairButton = !canShowDockButton && enterprise.canRepairProvisionally();
		repairButtonDocksAtStarbase = canShowDockButton;
		view.setRepairButtonCss(canShowDockButton?"has-dock":"has-repair");
		//this has to go after setting the CSS or CSS will be overwritten
		view.setRepairButtonEnabled(canShowDockButton || canShowRepairButton);
	}

	public void updateStatusButton() {
		Enterprise enterprise = application.starMap.enterprise;
		String cssImpulse = CSS.damageClass(enterprise.getImpulse().health());
		String cssTactical = CSS.damageClass(enterprise.getAutoAim().health());
		String cssPhasers = CSS.damageClass(enterprise.getPhasers().health());
		String cssTorpedos = CSS.damageClass(enterprise.getTorpedos().health());
		view.updateShortStatus(cssImpulse, cssTactical, cssPhasers, cssTorpedos);
		if (enterprise.getLrs().isEnabled())
			view.enableLlrsButton();
		else
			view.disableLrsButton();
	}

	@Override
	public void onTurnStarted(TurnStartedEvent evt) {
		GWT.log("tesk");
		updateStarDate();
		updateShieldsView();
		updateQuadrantHeader();
		updateAntimatter();
		updateScore();
		updateRepairButton();
	}

	public void updateAntimatter() {
		Setting antimatter = application.starMap.enterprise.getAntimatter();
		view.updateAntimatter((int) antimatter.getValue(), (int) antimatter.getMaximum());
	}

	public void updateQuadrantHeader() {
		String alert = "";
		Quadrant q = application.starMap.enterprise.getQuadrant();
		Enterprise e = application.starMap.enterprise;
		Location el = e.getLocation();
		if (!q.getKlingons().isEmpty()) {
			double minDistance = 3 * 3;
			for (Klingon k : q.getKlingons()) {
				minDistance = Math.min(minDistance,
						StarMap.distance_squared(el.getX(), el.getY(), k.getLocation().getX(), k.getLocation().getY()));
				if (minDistance<9)
					break; // 9 is read alert, can't get worse than that so no point in iterating further
			}
			alert = minDistance < 9 ? "red-alert" : "yellow-alert";
		}

		view.setQuadrantName(q.getName(), alert);
	}

	public void updateShieldsView() {
		Enterprise enterprise = application.starMap.enterprise;
		Setting shields = enterprise.getShields();
		view.updateShields((int) shields.getValue(), (int) shields.getCurrentUpperBound(), (int) shields.getMaximum());
	}

	public void onRepairButtonClicked() {
		if (repairButtonDocksAtStarbase)
			dockInStarbase();
		else repairProvisionally();
	}
	
	public void repairProvisionally() {
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
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		switch (event.getValue()) {
			case "computer": showScreen(); break;
			case "appmenu": break; //appmenu uses history tokens to allow hiding with "back" button, but isn't really a screen - so don't hide
			default: hideScreen();
		}
	}

	@Override
	public void onEnterpriseRepaired(Enterprise enterprise) {
		updateStatusButton();
		updateRepairButton();
	}

	@Override
	public void onEnterpriseDamaged(Enterprise enterprise) {
		updateStatusButton();
		updateRepairButton();
	}
	
	@Override
	public void onGameStarted(GameStartedEvent evt) {
		updateRepairButton();
		updateStatusButton();
	}
}
