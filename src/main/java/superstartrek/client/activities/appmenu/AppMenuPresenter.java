package superstartrek.client.activities.appmenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.model.Setting;

public class AppMenuPresenter extends BasePresenter<AppMenuActivity> implements AppMenuHandler, ValueChangeHandler<String> {

	String gotoStateAfterMenuHidden;
	
	public AppMenuPresenter(Application application) {
		super(application);
		application.events.addHandler(AppMenuEvent.TYPE, this);
		application.addHistoryListener(this);
	}

	@Override
	public void showMenu() {
		AppMenuView view = (AppMenuView)getView();
		gotoStateAfterMenuHidden = "computer";
		view.setMenuEntryEnabled("cmd_autoaim", application.starMap.enterprise.getAutoAim().getBooleanValue());
		view.show();
	}

	@Override
	public void hideMenu() {
		if (getView().isVisible())
			getView().hide();
	}
	
	public void onMenuHidden() {
		if (gotoStateAfterMenuHidden!=null)
			History.newItem(gotoStateAfterMenuHidden);
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
			//hideMenu called implicitly through history change event
			gotoStateAfterMenuHidden = "manual";
			break;
		}

	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		if (event.getValue().equals("appmenu"))
			showMenu();
		else hideMenu();
	}
}
