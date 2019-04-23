package superstartrek.client.activities.appmenu;

import com.google.gwt.user.client.Window;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.model.Setting;

public class AppMenuPresenter extends BasePresenter<AppMenuActivity> implements AppMenuHandler {

	public AppMenuPresenter(Application application) {
		super(application);
		application.events.addHandler(AppMenuEvent.TYPE, this);
	}

	@Override
	public void showMenu() {
		AppMenuView view = (AppMenuView)getView();
		view.setMenuEntryEnabled("cmd_autoaim", application.starMap.enterprise.getAutoAim().getBooleanValue());
		view.show();
	}

	@Override
	public void hideMenu() {
		getView().hide();
	}
	
	public void toggleAutoAim() {
		Setting autoaim = application.starMap.enterprise.getAutoAim();
		autoaim.setValue(!autoaim.getBooleanValue() && autoaim.isEnabled());
	}
	
	public void restart() {
		//TODO: break API dependency
		boolean v = Window.confirm("All progress will be lost. Continue?");
		if (v)
			application.reload();
	}

	public void onMenuItemClicked(String id) {
		switch (id) {
		case "cmd_autoaim":
			toggleAutoAim();
			hideMenu();
			break;
		case "cmd_restart":
			hideMenu();
			restart();
			break;
		case "cmd_manual":
			hideMenu();
			break;
		}

	}
}
