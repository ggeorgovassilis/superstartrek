package superstartrek.client.activities.appinstallation;

import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.popup.PopupViewPresenter;
import superstartrek.client.activities.pwa.ApplicationLifecycleHandler;
import superstartrek.client.eventbus.Events;
import superstartrek.client.uihandler.InteractionHandler;
import superstartrek.client.utils.Strings;

public class AppInstallPromptPresenter extends BasePresenter<AppInstallPromptView>
		implements ApplicationLifecycleHandler, PopupViewPresenter<AppInstallPromptView>, InteractionHandler {

	public AppInstallPromptPresenter() {
		addHandler(Events.SHOW_APP_INSTALL_PROMPT);
		addHandler(Events.INTERACTION);
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

	public void userClickedInstallButton() {
		view.hide();
		getApplication().pwa.installApplication();
	}

	@Override
	public void onUiInteraction(String tag) {
		switch (tag) {
		case "install-yes":
			userClickedInstallButton();
			break;
		case "install-no":
			cancelButtonClicked();
			break;
		case "install-never":
			userDoesntWantToInstallAppEver();
			break;
		}
	}

}
