package superstartrek.client.activities.computer;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.CSS;
import superstartrek.client.activities.combat.CombatHandler;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.sector.contextmenu.ContextMenuHideHandler;
import superstartrek.client.activities.sector.contextmenu.SectorSelectedHandler;
import superstartrek.client.bus.Commands;
import superstartrek.client.bus.Events;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.KeyPressedEventHandler;
import superstartrek.client.control.ScoreKeeper;
import superstartrek.client.model.Constants;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Setting;
import superstartrek.client.model.StarBase;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;
import superstartrek.client.model.Weapon;
import superstartrek.client.model.Enterprise.ShieldDirection;

public class ComputerPresenter extends BasePresenter<IComputerScreen>
		implements GamePhaseHandler, CombatHandler, ValueChangeHandler<String>,
		KeyPressedEventHandler, SectorSelectedHandler, ContextMenuHideHandler {

	ScoreKeeper scoreKeeper;
	Enterprise enterprise;
	StarMap starMap;

	public ComputerPresenter(Application application, ScoreKeeper scoreKeeper) {
		super(application);
 		this.scoreKeeper = scoreKeeper;
		application.browserAPI.addHistoryListener(this);
		addHandler(Events.PLAYER_TURN_STARTED, this);
		addHandler(Events.TURN_ENDED, this);
		addHandler(Events.KLINGON_DESTROYED, this);
		addHandler(Events.GAME_STARTED, this);
		addHandler(Events.KEY_PRESSED, this);
		addHandler(Events.SECTOR_SELECTED, this);
		addHandler(Events.CONTEXT_MENU_HIDE, this);
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	public void setStarMap(StarMap starMap) {
		this.starMap = starMap;
	}

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

	public void hideScreen() {
		view.hide();
	}

	String damageClass(Setting setting, boolean load) {
		String css = load?CSS.damageClass(setting):CSS.getOfflineDamageClass();
		if (setting.health()<1)
			css="damaged "+css;
		return css;
	}

	
	public void updateStatusButtonView() {
		String cssImpulse = damageClass(enterprise.getImpulse(), true);
		String cssTactical = damageClass(enterprise.getAutoAim(), true);
		String cssPhasers = damageClass(enterprise.getPhasers(), true);
		String cssTorpedos = damageClass(enterprise.getTorpedos(), enterprise.getTorpedos().getValue()>=1);
		view.updateShortStatus(cssImpulse, cssTactical, cssPhasers, cssTorpedos);
		if (enterprise.getLrs().isOperational())
			view.enableLlrsButton();
		else
			view.disableLrsButton();
	}

	public void updateAntimatterView() {
		Setting antimatter = enterprise.getAntimatter();
		view.updateAntimatter(antimatter.getValue(), antimatter.getMaximum());
		if (antimatter.getValue()<antimatter.getMaximum()*Constants.ANTIMATTER_WARNING_THRESHOLD)
			view.addAntimatterCss("antimatter-low");
		else view.removeAntimatterCss("antimatter-low");
	}

	public void updateQuadrantHeaderView() {
		String alert = "";
		Quadrant quadrant = enterprise.getQuadrant();
		Location enterprisePosition = enterprise.getLocation();
		final double distanceOfRedAlertSquared = 3 * 3;
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

		view.setQuadrantName(quadrant.getName(), alert);
	}

	public void updateShieldsView() {
		Setting shields = enterprise.getShields();
		view.updateShields(shields.getValue(), shields.getCurrentUpperBound(), shields.getMaximum());
		updateShieldsDirectionCss(enterprise.getShieldDirection());
	}

	public boolean canShowDockButton() {
		Quadrant q = enterprise.getQuadrant();
		StarBase starBase = q.getStarBase();
		boolean visible = starBase != null
				&& (!q.hasKlingons() || StarMap.within_distance(enterprise, starBase, 2));
		return visible;
	}

	public void onSkipButtonClicked() {
		application.eventBus.fireEvent(Events.TURN_YIELDED, (h)->h.onTurnYielded());
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
	public void onTurnEnded() {
		updateButtonViews();
	}

	@Override
	public void afterFire(Quadrant quadrant, Vessel actor, Thing target, Weapon weapon, double damage, boolean wasAutoFire) {
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
		view.setCommandBarMode("mode-command");
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
			if (application.starMap.enterprise.getLrs().isOperational())
				application.browserAPI.postHistoryChange("longrangescan");
			break;
		case 's':
		case 'S':
			onSkipButtonClicked();
			break;
		case KeyCodes.KEY_BACKSPACE:
		case 'b':
		case 'B':
			application.browserAPI.postHistoryChange("computer");
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
		Quadrant q = quadrant;
		Thing thing = q.findThingAt(location);
		String name = Thing.isVisible(thing)?thing.getName():"Nothing";
		view.setScanProperty("scan-report-name", "scan-report-name-value", "", name+" at "+location.toString());
		if (Thing.isVisible(thing) && Vessel.is(thing)) {
			Vessel vessel = thing.as();
			view.setScanProperty("scan-report-shields", "scan-report-shields-value", "", "%"+vessel.getShields().percentage());
			if (Klingon.is(thing)) {
				Klingon k = vessel.as();
				view.setScanProperty("scan-report-weapons", "scan-report-weapons-value", k.getDisruptor().isOperational()?"":"damage-offline", k.getDisruptor().isOperational()?"online":"offline");
				view.setScanProperty("scan-report-cloak", "scan-report-cloak-value", k.getCloak().isBroken()?"damage-offline":"", k.getCloak().isBroken()?"offline":"online");
			} else
			if (Enterprise.is(thing)) {
				Enterprise e = vessel.as();
				view.setScanProperty("scan-report-weapons", "scan-report-weapons-value", e.getPhasers().isOperational()?"":"damage-offline", e.getPhasers().isOperational()?"online":"offline");
			}
			view.setScanProperty("scan-report-engines", "scan-report-engines-value", vessel.getImpulse().isOperational()?"":"damage-offline", vessel.getImpulse().isOperational()?"online":"offline");
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
		for (ShieldDirection d:ShieldDirection.values())
			view.removeShieldCss("shield-"+d);
		view.addShieldCss("shield-"+direction);
	}
	
	public void onToggleShieldsButtonClicked() {
		enterprise.toggleShields();
		updateShieldsDirectionCss(enterprise.getShieldDirection());
	}

}
