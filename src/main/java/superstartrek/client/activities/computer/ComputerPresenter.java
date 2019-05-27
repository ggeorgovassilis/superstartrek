package superstartrek.client.activities.computer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.CSS;
import superstartrek.client.activities.combat.CombatHandler;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.navigation.EnterpriseRepairedHandler;
import superstartrek.client.bus.Commands;
import superstartrek.client.bus.Events;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.KeyPressedEventHandler;
import superstartrek.client.control.ScoreKeeper;
import superstartrek.client.model.Constants;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.QuadrantIndex;
import superstartrek.client.model.Setting;
import superstartrek.client.model.StarBase;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;

public class ComputerPresenter extends BasePresenter<IComputerScreen>
		implements ComputerHandler, GamePhaseHandler, CombatHandler, ValueChangeHandler<String>,
		EnterpriseRepairedHandler, KeyPressedEventHandler {

	ScoreKeeper scoreKeeper;
	boolean repairButtonDocksAtStarbase = false;
	Enterprise enterprise;
	StarMap starMap;

	public ComputerPresenter(Application application, ScoreKeeper scoreKeeper) {
		super(application);
		GWT.log("_ss");
		this.scoreKeeper = scoreKeeper;
		application.browserAPI.addHistoryListener(this);
		addHandler(Commands.SHOW_COMPUTER, this);
		addHandler(Events.TURN_STARTED, this);
		addHandler(Events.KLINGON_DESTROYED, this);
		addHandler(Events.ENTERPRISE_DAMAGED, this);
		addHandler(Events.ENTERPRISE_REPAIRED, this);
		addHandler(Events.GAME_STARTED, this);
		addHandler(Events.KEY_PRESSED, this);
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
		view.setRepairButtonCss(canShowDockButton ? "has-dock" : "has-repair");
		// this has to go after setting the CSS or CSS will be overwritten
		view.setRepairButtonEnabled(canShowDockButton || canShowRepairButton);
	}

	public void updateStatusButtonView() {
		String cssImpulse = CSS.damageClass(enterprise.getImpulse());
		String cssTactical = CSS.damageClass(enterprise.getAutoAim());
		String cssPhasers = CSS.damageClass(enterprise.getPhasers());
		String cssTorpedos = CSS.damageClass(enterprise.getTorpedos());
		view.updateShortStatus(cssImpulse, cssTactical, cssPhasers, cssTorpedos);
		if (enterprise.getLrs().isEnabled())
			view.enableLlrsButton();
		else
			view.disableLrsButton();
	}

	public void updateAntimatterView() {
		Setting antimatter = enterprise.getAntimatter();
		view.updateAntimatter((int) antimatter.getValue(), (int) antimatter.getMaximum());
		if (antimatter.getValue()<antimatter.getMaximum()*Constants.ANTIMATTER_WARNING_THRESHOLD)
			view.addAntimatterCss("antimatter-low");
		else view.removeAntimatterCss("antimatter-low");
	}

	public void updateQuadrantHeaderView() {
		String alert = "";
		Quadrant quadrant = enterprise.getQuadrant();
		Location enterprisePosition = enterprise.getLocation();
		final double distanceOfRedAlertSquared = 3 * 3;
		if (!quadrant.getKlingons().isEmpty()) {
			double minDistanceSquared = distanceOfRedAlertSquared;
			for (Klingon k : quadrant.getKlingons()) {
				minDistanceSquared = Math.min(minDistanceSquared, StarMap.distance_squared(enterprisePosition.getX(),
						enterprisePosition.getY(), k.getLocation().getX(), k.getLocation().getY()));
				if (minDistanceSquared < distanceOfRedAlertSquared)
					break; // 9 is red alert, can't get worse than that so no point in iterating further
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
			Location loc = starMap.findFreeSpotAround(new QuadrantIndex(q, starMap), q.getStarBase().getLocation(), 2);
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
		application.eventBus.fireEvent(Events.TURN_YIELDED, (h)->h.onTurnYielded());
	}

	@Override
	public void onTurnStarted() {
		updateStarDateView();
		updateShieldsView();
		updateQuadrantHeaderView();
		updateAntimatterView();
		updateScoreView();
		updateButtonViews();
	}

	public void onRepairButtonClicked() {
		if (repairButtonDocksAtStarbase)
			dockInStarbase();
		else
			repairProvisionally();
	}

	@Override
	public void afterFire(Quadrant quadrant, Vessel actor, Thing target, String weapon, double damage, boolean wasAutoFire) {
		if (target == starMap.enterprise) {
			updateShieldsView();
		}
	}

	@Override
	public void onVesselDestroyed(Vessel vessel) {
		updateQuadrantHeaderView();
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		switch (event.getValue()) {
		case "computer":
			showScreen();
			break;
		case "appmenu":
			break; // appmenu uses history tokens to allow hiding with "back" button, but isn't
					// really a screen - so don't hide
		default:
			hideScreen();
		}
	}
	
	protected void updateButtonViews() {
		updateStatusButtonView();
		updateRepairButtonView();
	}

	@Override
	public void onEnterpriseRepaired(Enterprise enterprise) {
		updateButtonViews();
		updateAntimatterView();
	}

	@Override
	public void onEnterpriseDamaged(Enterprise enterprise) {
		updateButtonViews();
	}

	@Override
	public void onGameStarted(StarMap starMap) {
		this.enterprise = starMap.enterprise;
		this.starMap = starMap;
		updateButtonViews();
	}

	@Override
	public void onKeyPressed(int code) {
		switch(code) {
		case 'l':
		case 'L':
			if (application.starMap.enterprise.getLrs().isEnabled())
				application.browserAPI.postHistoryChange("longrangescan");
			break;
		case 's':
		case 'S':
			onSkipButtonClicked();
			break;
		case 'r':
		case 'R':
			onRepairButtonClicked();
			break;
		case KeyCodes.KEY_BACKSPACE:
		case 'b':
		case 'B':
			application.browserAPI.postHistoryChange("computer");
			break;
		}
	}
}
