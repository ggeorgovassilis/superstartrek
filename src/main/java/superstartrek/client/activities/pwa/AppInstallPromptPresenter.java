package superstartrek.client.activities.pwa;

import java.util.logging.Logger;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.PopupViewPresenter;
import superstartrek.client.utils.Strings;

public class AppInstallPromptPresenter extends BasePresenter<AppInstallPromptView> implements ApplicationLifecycleHandler, PopupViewPresenter<AppInstallPromptView>{

	private static Logger log = Logger.getLogger("");

	public AppInstallPromptPresenter(Application application) {
		super(application);
		application.events.addHandler(ApplicationLifecycleEvent.TYPE, this);
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
		log.info("Show app install prompt");
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
		application.pwa.installApplication();
	}

}
