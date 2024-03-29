package superstartrek.client.activities.appmenu;

import superstartrek.client.activities.ActivityChangedHandler;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.popup.PopupViewPresenter;
import superstartrek.client.eventbus.Events;
import superstartrek.client.space.Setting;
import superstartrek.client.uihandler.InteractionHandler;

public class AppMenuPresenter extends BasePresenter<AppMenuView>
		implements PopupViewPresenter<AppMenuView>, ActivityChangedHandler, InteractionHandler {

	String gotoStateAfterMenuHidden;

	public AppMenuPresenter() {
		addHandler(Events.ACTIVITY_CHANGED);
		addHandler(Events.INTERACTION);
	}

	public void updateCommands() {
		Setting autoAim = getEnterprise().getAutoAim();
		view.setMenuEntryEnabled("cmd_autoaim", autoAim.isOperational());

		Setting evasiveManeuvers = getEnterprise().getEvasiveManeuvers();
		view.setMenuEntryEnabled("cmd_evasive_maneuvers", evasiveManeuvers.isOperational());
	}

	public void showMenu() {
		if (view.isVisible())
			return;
		gotoStateAfterMenuHidden = "computer";
		updateCommands();
		view.show();
	}

	public void hideMenu() {
		if (view.isVisible())
			view.hide();
	}

	public void onMenuHidden() {
		if (gotoStateAfterMenuHidden != null)
			getApplication().browserAPI.postHistoryChange(gotoStateAfterMenuHidden);
	}

	public void toggleAutoAim() {
		Setting autoaim = getEnterprise().getAutoAim();
		autoaim.setValue(!autoaim.isBroken() && !autoaim.getBooleanValue());
	}

	public void toggleEvasiveManeuvers() {
		Setting evasiveManeuvers = getEnterprise().getEvasiveManeuvers();
		boolean b1 = !evasiveManeuvers.isBroken();
		boolean b2 = !evasiveManeuvers.getBooleanValue();
		evasiveManeuvers.setValue(b1 && b2);
	}

	public void restart() {
		getApplication().browserAPI.confirm("All progress will be lost. Continue?", (result) -> {
			if (result)
				getApplication().restart();
		});
	}

	public void onMenuItemClicked(String id) {
		switch (id) {
		case "cmd_autoaim":
			toggleAutoAim();
			updateCommands();
			break;
		case "cmd_restart":
			restart();
			break;
		case "cmd_settings":
			// hideMenu called implicitly through history change event
			gotoStateAfterMenuHidden = "settings";
			break;
		case "cmd_manual":
			gotoStateAfterMenuHidden = "manual";
			break;
		case "cmd_evasive_maneuvers":
			toggleEvasiveManeuvers();
			updateCommands();
			break;
		}
		view.hide();
	}

	@Override
	public void onActivityChanged(String activity) {
		if (activity.equals("appmenu"))
			showMenu();
		else
			hideMenu();
	}

	@Override
	public void cancelButtonClicked() {
		hideMenu();
	}

	@Override
	public void onUiInteraction(String tag) {
		onMenuItemClicked(tag);
	}

}
