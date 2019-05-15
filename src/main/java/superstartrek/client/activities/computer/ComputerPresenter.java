package superstartrek.client.activities.computer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.CSS;
import superstartrek.client.activities.combat.EnterpriseDamagedHandler;
import superstartrek.client.activities.combat.FireHandler;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.KlingonDestroyedHandler;
import superstartrek.client.activities.navigation.EnterpriseRepairedHandler;
import superstartrek.client.control.GamePhaseHandler;
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
	Enterprise enterprise;
	StarMap starMap;
	
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
	
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	
	public void setStarMap(StarMap starMap) {
		this.starMap = starMap;
	}

	@Override
	public void showScreen() {
		updateStarDateView();
		view.show();
	}

	public void updateStarDateView() {
		view.showStarDate("" + starMap.getStarDate());
	}

	public void updateScoreView() {
		view.showScore("" + scoreKeeper.getScore());
	}

	@Override
	public void hideScreen() {
		view.hide();
	}
	
	public void updateRepairButtonView() {
		boolean canShowDockButton = canShowDockButton();
		boolean canShowRepairButton = !canShowDockButton && enterprise.canRepairProvisionally();
		repairButtonDocksAtStarbase = canShowDockButton;
		view.setRepairButtonCss(canShowDockButton?"has-dock":"has-repair");
		//this has to go after setting the CSS or CSS will be overwritten
		view.setRepairButtonEnabled(canShowDockButton || canShowRepairButton);
	}

	public void updateStatusButtonView() {
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


	public void updateAntimatterView() {
		Setting antimatter = enterprise.getAntimatter();
		view.updateAntimatter((int) antimatter.getValue(), (int) antimatter.getMaximum());
	}

	public void updateQuadrantHeaderView() {
		String alert = "";
		Quadrant quadrant = enterprise.getQuadrant();
		Location enterprisePosition = enterprise.getLocation();
		final double distanceOfRedAlertSquared = 3*3;
		if (!quadrant.getKlingons().isEmpty()) {
			double minDistanceSquared = distanceOfRedAlertSquared;
			for (Klingon k : quadrant.getKlingons()) {
				minDistanceSquared = Math.min(minDistanceSquared,
						StarMap.distance_squared(enterprisePosition.getX(), enterprisePosition.getY(), k.getLocation().getX(), k.getLocation().getY()));
				if (minDistanceSquared<distanceOfRedAlertSquared)
					break; // 9 is read alert, can't get worse than that so no point in iterating further
			}
			alert = minDistanceSquared < distanceOfRedAlertSquared ? "red-alert" : "yellow-alert";
		}

		view.setQuadrantName(quadrant.getName(), alert);
	}

	public void updateShieldsView() {
		Setting shields = enterprise.getShields();
		view.updateShields((int) shields.getValue(), (int) shields.getCurrentUpperBound(), (int) shields.getMaximum());
	}

	public void dockInStarbase() {
		Quadrant q = enterprise.getQuadrant();
		boolean inRange = StarMap.within_distance(enterprise, q.getStarBase(), 1.1);
		if (!inRange) {
			Location loc = starMap.findFreeSpotAround(q, q.getStarBase().getLocation(), 2);
			if (loc == null) {
				application.message("No space around starbase");
				return;
			}
			enterprise.moveToIgnoringConstraints(loc);
		}
		enterprise.dockAtStarbase(q.getStarBase());
	}
	
	public boolean canShowDockButton() {
		Quadrant q = enterprise.getQuadrant();
		StarBase starBase = q.getStarBase();
		boolean visible = starBase != null
				&& (q.getKlingons().isEmpty() || StarMap.within_distance(enterprise, starBase, 2));
		return visible;
	}

	public void repairProvisionally() {
		starMap.enterprise.repairProvisionally();
	}

	public void onSkipButtonClicked() {
		application.events.fireEvent(new YieldTurnEvent());
	}

	@Override
	public void onTurnStarted(TurnStartedEvent evt) {
		updateStarDateView();
		updateShieldsView();
		updateQuadrantHeaderView();
		updateAntimatterView();
		updateScoreView();
		updateRepairButtonView();
	}

	public void onRepairButtonClicked() {
		if (repairButtonDocksAtStarbase)
			dockInStarbase();
		else repairProvisionally();
	}
	
	@Override
	public void afterFire(FireEvent evt) {
		if (evt.target == starMap.enterprise) {
			updateShieldsView();
		}
	}

	@Override
	public void onKlingonDestroyed(Klingon klingon) {
		updateQuadrantHeaderView();
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
		updateStatusButtonView();
		updateRepairButtonView();
	}

	@Override
	public void onEnterpriseDamaged(Enterprise enterprise) {
		updateStatusButtonView();
		updateRepairButtonView();
	}
	
	@Override
	public void onGameStarted(GameStartedEvent evt) {
		this.enterprise = evt.starMap.enterprise;
		this.starMap = evt.starMap;
		updateStatusButtonView();
		updateRepairButtonView();
	}
}
