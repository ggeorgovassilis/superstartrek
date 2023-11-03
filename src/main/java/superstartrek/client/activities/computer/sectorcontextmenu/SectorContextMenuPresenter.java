package superstartrek.client.activities.computer.sectorcontextmenu;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import superstartrek.client.activities.ActivityChangedHandler;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.utils.BrowserAPI;
import superstartrek.client.vessels.CombatHandler;
import superstartrek.client.vessels.Enterprise;
import superstartrek.client.vessels.CombatHandler.partTarget;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.KeyPressedEventHandler;
import superstartrek.client.eventbus.Events;
import superstartrek.client.eventbus.EventsMixin;
import superstartrek.client.space.Location;
import superstartrek.client.space.Quadrant;

public class SectorContextMenuPresenter extends BasePresenter<SectorContextMenuView> implements SectorSelectedHandler,
		GamePhaseHandler, ActivityChangedHandler, ContextMenuHideHandler, KeyPressedEventHandler, EventsMixin {

	Location sector;
	Quadrant quadrant;
	Map<String, Boolean> buttonsEnabled = new HashMap<>();

	final static String cmd_navigate = "cmd_navigate";
	final static String cmd_firePhasers = "cmd_firePhasers";
	final static String cmd_fireTorpedos = "cmd_fireTorpedos";
	final static String cmd_dockStarbase = "cmd_dockStarbase";
	final static String cmd_precision_weapons = "cmd_precision_weapons";
	final static String cmd_precision_propulsion = "cmd_precision_propulsion";

	public SectorContextMenuPresenter() {
		addHandler(Events.SECTOR_SELECTED);
		addHandler(Events.TURN_ENDED);
		addHandler(Events.CONTEXT_MENU_HIDDEN);
		addHandler(Events.ACTIVITY_CHANGED);
		buttonsEnabled.put(cmd_navigate, false);
		buttonsEnabled.put(cmd_firePhasers, false);
		buttonsEnabled.put(cmd_fireTorpedos, false);
		buttonsEnabled.put(cmd_dockStarbase, true);
		buttonsEnabled.put(cmd_precision_weapons, false);
		buttonsEnabled.put(cmd_precision_propulsion, false);

	}

	public void showMenuImmediatelly(int screenX, int screenY, Location sector, Quadrant quadrant) {
		SectorContextMenuPresenter.this.quadrant = quadrant;
		Enterprise enterprise = getEnterprise();
		// read dimensions before modifying the DOM to avoid re-layout

		boolean canFirePhaserAt = enterprise.canFirePhaserAt(sector) == null;
		buttonsEnabled.put(cmd_navigate, enterprise.canNavigateTo(sector));
		buttonsEnabled.put(cmd_firePhasers, canFirePhaserAt);
		buttonsEnabled.put(cmd_precision_weapons, canFirePhaserAt);
		buttonsEnabled.put(cmd_precision_propulsion, canFirePhaserAt);
		buttonsEnabled.put(cmd_fireTorpedos,
				enterprise.getTorpedos().isOperational() && enterprise.getTorpedos().getValue() >= 1);
		for (String cmd : buttonsEnabled.keySet())
			view.enableButton(cmd, buttonsEnabled.get(cmd));
		// if the menu is too close to the screen borders it might be cut off and not
		// all buttons are visible
		// this is some heavy heuristics, because the menu has a "fixed" size (in em
		// units)
		// that's empirical knowledge from the CSS
		int menuWidthEm = 8;
		int menuHeightEm = 5;

		BrowserAPI browserAPI = getApplication().browserAPI;
		int horizEmToPx = browserAPI.getMetricWidthInPx();
		int vertEmToPx = browserAPI.getMetricHeightInPx();
		int screen_width_em = browserAPI.getWindowWidthPx() / horizEmToPx;

		int target_x_em = Math.max(screenX / horizEmToPx, menuWidthEm / 2);
		target_x_em = Math.min(target_x_em, screen_width_em - menuWidthEm);
		int target_x_px = target_x_em * horizEmToPx;

		int target_y_em = Math.max(screenY / vertEmToPx, menuHeightEm / 2);
		int target_y_px = target_y_em * vertEmToPx;
		boolean isStarbaseAtTarget = quadrant.getStarBase() != null
				&& (quadrant.getStarBase() == quadrant.findThingAt(sector));
		view.enableDockWithStarbaseButton(isStarbaseAtTarget);

		view.setLocation(target_x_px, target_y_px);
		view.show();
		addHandler(Events.KEY_PRESSED);
	}

	public void showMenu(int screenX, int screenY, Location sector, Quadrant quadrant) {
		// in case the menu is visible already, this call will hide it first and then
		// show it again (with animations)
		// at the new location
		hideMenu(() -> showMenuImmediatelly(screenX, screenY, sector, quadrant));
	}

	@Override
	public void onSectorSelected(Location sector, Quadrant quadrant, int screenX, int screenY) {
		this.sector = sector;
		if (view.isVisible())
			hideMenu(() -> {
				getEvents().fireEvent(Events.CONTEXT_MENU_HIDDEN, (v) -> v.onMenuHidden());
			});
		else
			showMenu(screenX, screenY, sector, quadrant);
	}

	public void onEscapePressed() {
		hideMenu(null);
	}

	protected void hideMenu(ScheduledCommand callback) {
		if (view.isVisible())
			getEvents().fireEvent(Events.CONTEXT_MENU_HIDE, (h) -> h.onStartToHideMenu());
		removeHandler(Events.KEY_PRESSED, this);
		view.hide(callback);
	}

	public void onMenuClicked() {
		hideMenu(null);
	}

	public void onCommandClicked(String command) {
		Enterprise enterprise = getEnterprise();
		if (!buttonsEnabled.get(command))
			return;
		hideMenu(() -> {
			switch (command) {
			case cmd_navigate:
				enterprise.navigateTo(sector);
				break;
			case cmd_firePhasers:
				enterprise.firePhasersAt(sector, false, partTarget.none);
				break;
			case cmd_fireTorpedos:
				enterprise.fireTorpedosAt(sector);
				break;
			case cmd_dockStarbase:
				enterprise.dockInStarbase();
				break;
			case cmd_precision_weapons:
				enterprise.firePhasersAt(sector, false, CombatHandler.partTarget.weapons);
				break;
			case cmd_precision_propulsion:
				enterprise.firePhasersAt(sector, false, CombatHandler.partTarget.propulsion);
				break;
			}
		});
	}

	@Override
	public void onTurnEnded() {
		hideMenu(null);
	}

	@Override
	public void onActivityChanged(String activity) {
		hideMenu(null);
	}

	@Override
	public void onMenuHidden() {
		if (view.isVisible())
			hideMenu(null);
	}

	@Override
	public void onKeyPressed(int code) {
		if (!view.isVisible())
			return;
		switch (code) {
		case 'n':
		case 'N':
			onCommandClicked(cmd_navigate);
			break;
		case 'p':
		case 'P':
			onCommandClicked(cmd_firePhasers);
			break;
		case 't':
		case 'T':
			onCommandClicked(cmd_fireTorpedos);
			break;
		case KeyCodes.KEY_ESCAPE:
			hideMenu(null);
		}
	}

}
