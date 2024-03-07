package superstartrek.client.activities.computer;

import com.google.gwt.event.dom.client.KeyCodes;
import superstartrek.client.Application;
import superstartrek.client.activities.ActivityChangedHandler;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.computer.sectorcontextmenu.ContextMenuHideHandler;
import superstartrek.client.activities.computer.sectorcontextmenu.SectorSelectedHandler;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.KeyPressedEventHandler;
import superstartrek.client.control.ScoreKeeper;
import superstartrek.client.eventbus.Events;
import superstartrek.client.space.Constants;
import superstartrek.client.space.Location;
import superstartrek.client.space.Quadrant;
import superstartrek.client.space.Setting;
import superstartrek.client.space.StarMap;
import superstartrek.client.space.Thing;
import superstartrek.client.uihandler.InteractionHandler;
import superstartrek.client.utils.CSS;
import superstartrek.client.vessels.CombatHandler;
import superstartrek.client.vessels.Enterprise;
import superstartrek.client.vessels.Klingon;
import superstartrek.client.vessels.Vessel;
import superstartrek.client.vessels.Weapon;
import superstartrek.client.vessels.Enterprise.ShieldDirection;

public class ComputerPresenter extends BasePresenter<ComputerScreen>
		implements GamePhaseHandler, CombatHandler, ActivityChangedHandler, KeyPressedEventHandler,
		SectorSelectedHandler, ContextMenuHideHandler, InteractionHandler {

	ScoreKeeper scoreKeeper;

	public ComputerPresenter(Application application, ScoreKeeper scoreKeeper) {
		super();
		this.scoreKeeper = scoreKeeper;
		addHandler(Events.ACTIVITY_CHANGED);
		addHandler(Events.PLAYER_TURN_STARTED);
		addHandler(Events.KLINGON_DESTROYED);
		addHandler(Events.KEY_PRESSED);
		addHandler(Events.SECTOR_SELECTED);
		addHandler(Events.CONTEXT_MENU_HIDE);
		addHandler(Events.INTERACTION);

	}

	void showScreen() {
		updateStarDateView();
		view.show();
	}

	void updateStarDateView() {
		view.showStarDate("" + getStarMap().getStarDate());
	}

	void updateScoreView() {
		view.showScore("" + scoreKeeper.getScore());
	}

	void hideScreen() {
		view.hide();
	}

	String damageClass(Setting setting, boolean damage) {
		String css = damage ? CSS.damageClass(setting) : CSS.getOfflineDamageClass();
		if (setting.health() < 1)
			css = "damaged " + css;
		return css;
	}

	public void updateStatusButtonView() {
		Enterprise enterprise = getEnterprise();
		String cssImpulse = damageClass(enterprise.getImpulse(), true);
		String cssTactical = damageClass(enterprise.getAutoAim(), true);
		String cssPhasers = damageClass(enterprise.getPhasers(), true);
		String cssTorpedos = damageClass(enterprise.getTorpedos(), enterprise.getTorpedos().getValue() >= 1);
		view.updateShortStatus(cssImpulse, cssTactical, cssPhasers, cssTorpedos);
		view.updateTorpedoLabel("Torpedos " + enterprise.getTorpedos().getValue());
		if (enterprise.getLrs().isOperational())
			view.enableLlrsButton();
		else
			view.disableLrsButton();
	}

	void updateAntimatterView() {
		Setting antimatter = getEnterprise().getAntimatter();
		view.updateAntimatter(antimatter.getValue(), antimatter.getMaximum());
		if (antimatter.getValue() < antimatter.getMaximum() * Constants.ANTIMATTER_WARNING_THRESHOLD)
			view.addAntimatterCss("antimatter-low");
		else
			view.removeAntimatterCss("antimatter-low");
	}

	String getAlertCss() {
		Quadrant quadrant = getEnterprise().getQuadrant();
		Location enterprisePosition = getEnterprise().getLocation();
		String alert = "";
		final double distanceOfRedAlertSquared = Constants.RED_ALERT_DISTANCE * Constants.RED_ALERT_DISTANCE;
		if (quadrant.hasKlingons()) {
			double minDistanceSquared = distanceOfRedAlertSquared;
			for (Klingon k : quadrant.getKlingons()) {
				minDistanceSquared = Math.min(minDistanceSquared, StarMap.distance_squared(enterprisePosition.x,
						enterprisePosition.y, k.getLocation().x, k.getLocation().y));
				if (minDistanceSquared < distanceOfRedAlertSquared)
					break; // 9 is red alert, can't get worse than that so no point in iterating further
			}
			alert = minDistanceSquared < distanceOfRedAlertSquared ? "red-alert" : "yellow-alert";
		}
		return alert;
	}

	public void updateQuadrantHeaderView() {
		String alert = getAlertCss();
		view.setQuadrantName(getEnterprise().getQuadrant().getName(), alert);
	}

	public void updateShieldsView() {
		Setting shields = getEnterprise().getShields();
		view.updateShields(shields.getValue(), shields.getCurrentUpperBound(), shields.getMaximum());
		// TODO: updateShieldsView is called when player turn starts, but shields
		// directions change only when
		// the user toggles them or after a starbase dock.
		updateShieldsDirectionCss(getEnterprise().getShieldDirection());
	}

	public void onSkipButtonClicked() {
		getApplication().eventBus.fireEvent(Events.TURN_YIELDED, (h) -> h.onTurnYielded());
	}

	@Override
	public void onPlayerTurnStarted() {
		updateStarDateView();
		updateShieldsView();
		updateQuadrantHeaderView();
		updateAntimatterView();
		updateScoreView();
		updateButtonViews();
	}

	@Override
	public void afterFire(Quadrant quadrant, Vessel actor, Thing target, Weapon weapon, double damage,
			boolean wasAutoFire) {
		if (target == getEnterprise()) {
			updateShieldsView();
		}
	}

	@Override
	public void onVesselDestroyed(Vessel vessel) {
		updateQuadrantHeaderView();
	}

	@Override
	public void onActivityChanged(String activity) {
		switch (activity) {
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

	void updateButtonViews() {
		updateStatusButtonView();
		view.setCommandBarMode("mode-command");
	}

	@Override
	public void onKeyPressed(int code) {
		switch (code) {
		case 'l':
		case 'L':
			if (getEnterprise().getLrs().isOperational())
				getApplication().browserAPI.postHistoryChange("longrangescan");
			break;
		case 's':
		case 'S':
			onSkipButtonClicked();
			break;
		case KeyCodes.KEY_BACKSPACE:
		case 'b':
		case 'B':
			getApplication().browserAPI.postHistoryChange("computer");
			break;
		}
	}

	@Override
	public void onSectorSelected(Location sector, Quadrant quadrant, int screenX, int screenY) {
		view.setCommandBarMode("mode-scanner");
		scanSector(sector, quadrant);
	}

	// prints quick scan info (eg. enemy ship stats) on the command bar
	public void scanSector(Location location, Quadrant quadrant) {
		Thing thing = quadrant.findThingAt(location);
		String name = Thing.isVisible(thing) ? thing.getName() : "Nothing";
		view.setScanProperty("scan-report-name", "scan-report-name-value", "", name + " at " + location.toString());
		if (Thing.isVisible(thing) && Vessel.is(thing)) {
			Vessel vessel = thing.as();
			view.setScanProperty("scan-report-shields", "scan-report-shields-value", "",
					"%" + vessel.getShields().percentage());
			if (Klingon.is(thing)) {
				Klingon k = vessel.as();
				view.setScanProperty("scan-report-weapons", "scan-report-weapons-value",
						k.getDisruptor().isOperational() ? "" : "damage-offline",
						k.getDisruptor().isOperational() ? "online" : "offline");
				view.setScanProperty("scan-report-cloak", "scan-report-cloak-value",
						k.getCloak().isBroken() ? "damage-offline" : "",
						k.getCloak().isBroken() ? "offline" : "online");
			} else if (Enterprise.is(thing)) {
				Enterprise e = vessel.as();
				view.setScanProperty("scan-report-weapons", "scan-report-weapons-value",
						e.getPhasers().isOperational() ? "" : "damage-offline",
						e.getPhasers().isOperational() ? "online" : "offline");
			}
			view.setScanProperty("scan-report-engines", "scan-report-engines-value",
					vessel.getImpulse().isOperational() ? "" : "damage-offline",
					vessel.getImpulse().isOperational() ? "online" : "offline");
		} else {
			view.setScanProperty("scan-report-shields", "scan-report-shields-value", "hidden", "");
			view.setScanProperty("scan-report-weapons", "scan-report-weapons-value", "hidden", "");
			view.setScanProperty("scan-report-engines", "scan-report-engines-value", "hidden", "");
			view.setScanProperty("scan-report-cloak", "scan-report-cloak-value", "hidden", "");
		}
		view.show();
	}

	@Override
	public void onMenuHidden() {
		view.setCommandBarMode("mode-command");
	}

	@Override
	public void onStartToHideMenu() {
		view.setCommandBarMode("mode-command");
	}

	void updateShieldsDirectionCss(ShieldDirection direction) {
		for (ShieldDirection d : ShieldDirection.values())
			view.removeShieldCss("shield-" + d);
		view.addShieldCss("shield-" + direction);
	}

	public void onToggleShieldsButtonClicked() {
		getEnterprise().toggleShields();
		updateShieldsDirectionCss(getEnterprise().getShieldDirection());
	}

	@Override
	public void onUiInteraction(String tag) {
		if ("cmd_skip".equals(tag))
			onSkipButtonClicked();
		else if ("cmd_toggleShields".equals(tag))
			onToggleShieldsButtonClicked();
	}

}
