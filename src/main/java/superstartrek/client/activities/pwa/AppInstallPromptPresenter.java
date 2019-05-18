package superstartrek.client.activities.pwa;

import com.google.gwt.user.client.Cookies;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.PopupViewPresenter;
import superstartrek.client.bus.Events;
import superstartrek.client.utils.Strings;

public class AppInstallPromptPresenter extends BasePresenter<AppInstallPromptView> implements ApplicationLifecycleHandler, PopupViewPresenter<AppInstallPromptView>{

	public AppInstallPromptPresenter(Application application) {
		super(application);
		application.bus.register(Events.SHOW_APP_INSTALL_PROMPT, this);
	}
	
	public boolean didUserForbidInstallation() {
		String v = Cookies.getCookie("neverinstall");
		return !Strings.isEmpty(v);
	}
	
	public void rememberThatUserForbidsInstallation() {
		Cookies.setCookie("neverinstall", "never");
	}
	
	@Override
	public void showInstallPrompt() {
		if (didUserForbidInstallation())
			return;
		view.show();
	}

	@Override
	public void userWantsToDismissPopup() {
		view.hide();
	}
	
	public void userDoesntWantToInstallAppEver() {
		rememberThatUserForbidsInstallation();
		view.hide();
	}
	
	public void userClickedInstallButton(){
		view.hide();
		application.pwa.installApplication();
	}

}
