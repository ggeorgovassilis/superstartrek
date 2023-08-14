package superstartrek.client.activities.appmenu;

import superstartrek.client.Application;
import superstartrek.client.activities.ActivityChangedHandler;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.PopupViewPresenter;
import superstartrek.client.bus.Events;
import superstartrek.client.model.Setting;

public class AppMenuPresenter extends BasePresenter<IAppMenuView>
		implements PopupViewPresenter<IAppMenuView>, ActivityChangedHandler{

	String gotoStateAfterMenuHidden;

	public AppMenuPresenter(Application application) {
		super(application);
		addHandler(Events.ACTIVITY_CHANGED, this);

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
			application.browserAPI.postHistoryChange(gotoStateAfterMenuHidden);
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
		application.browserAPI.confirm("All progress will be lost. Continue?", (result) -> {
			if (result)
				application.restart();
		});
	}

	public void onMenuItemClicked(String id) {
		switch (id) {
		case "cmd_autoaim":
			toggleAutoAim();
			updateCommands();
			hideMenu();
			break;
		case "cmd_restart":
			hideMenu();
			restart();
			break;
		case "cmd_settings":
			gotoStateAfterMenuHidden = "settings";
			break;
		case "cmd_manual":
			// hideMenu called implicitly through history change event
			gotoStateAfterMenuHidden = "manual";
			break;
		case "cmd_evasive_maneuvers":
			toggleEvasiveManeuvers();
			updateCommands();
			hideMenu();
			break;
		}

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


}
