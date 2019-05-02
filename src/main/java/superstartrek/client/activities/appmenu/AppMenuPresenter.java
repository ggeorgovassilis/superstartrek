package superstartrek.client.activities.appmenu;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.PopupViewPresenter;
import superstartrek.client.activities.pwa.Callback;
import superstartrek.client.model.Setting;

public class AppMenuPresenter extends BasePresenter<AppMenuView>
		implements PopupViewPresenter<AppMenuView>, AppMenuHandler, ValueChangeHandler<String> {

	String gotoStateAfterMenuHidden;

	public AppMenuPresenter(Application application) {
		super(application);
		addHandler(AppMenuEvent.TYPE, this);
		application.browserAPI.addHistoryListener(this);
	}

	public void updateCommands() {
		view.setMenuEntryEnabled("cmd_autoaim", application.starMap.enterprise.getAutoAim().getBooleanValue());
	}

	@Override
	public void showMenu() {
		gotoStateAfterMenuHidden = "computer";
		updateCommands();
		view.show();
	}

	@Override
	public void hideMenu() {
		if (view.isVisible())
			view.hide();
	}

	public void onMenuHidden() {
		if (gotoStateAfterMenuHidden != null)
			application.browserAPI.postHistoryChange(gotoStateAfterMenuHidden);
	}

	public void toggleAutoAim() {
		Setting autoaim = application.starMap.enterprise.getAutoAim();
		autoaim.setValue(!autoaim.getBooleanValue() && autoaim.isEnabled());
	}

	public void restart() {
		application.browserAPI.confirm("All progress will be lost. Continue?", (result) -> {
			if (result)
				application.reload();
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
		case "cmd_manual":
			// hideMenu called implicitly through history change event
			gotoStateAfterMenuHidden = "manual";
			break;
		}

	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		if (event.getValue().equals("appmenu"))
			showMenu();
		else
			hideMenu();
	}

	@Override
	public void userWantsToDismissPopup() {
		hideMenu();
	}
}
