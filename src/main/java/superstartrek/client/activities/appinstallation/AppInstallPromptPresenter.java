package superstartrek.client.activities.appinstallation;

import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.PopupViewPresenter;
import superstartrek.client.activities.pwa.ApplicationLifecycleHandler;
import superstartrek.client.eventbus.Events;
import superstartrek.client.utils.Strings;

public class AppInstallPromptPresenter extends BasePresenter<AppInstallPromptView> implements ApplicationLifecycleHandler, PopupViewPresenter<AppInstallPromptView>{

	public AppInstallPromptPresenter() {
		addHandler(Events.SHOW_APP_INSTALL_PROMPT);
	}
	
	public boolean didUserForbidInstallation() {
		String v = getApplication().browserAPI.getCookie("neverinstall");
		return !Strings.isEmpty(v);
	}
	
	public void rememberThatUserForbidsInstallation() {
		getApplication().browserAPI.setCookie("neverinstall", "never");
	}
	
	@Override
	public void showInstallPrompt() {
		if (didUserForbidInstallation())
			return;
		view.show();
	}

	@Override
	public void cancelButtonClicked() {
		view.hide();
	}
	
	public void userDoesntWantToInstallAppEver() {
		rememberThatUserForbidsInstallation();
		view.hide();
	}
	
	public void userClickedInstallButton(){
		view.hide();
		getApplication().pwa.installApplication();
	}

}
