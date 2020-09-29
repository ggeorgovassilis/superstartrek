package superstartrek.client.activities.pwa;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.PopupViewPresenter;
import superstartrek.client.bus.Events;

public class UpdateAppPromptPresenter extends BasePresenter<UpdateAppPromptView> implements PopupViewPresenter<UpdateAppPromptView>, ApplicationLifecycleHandler{

	public UpdateAppPromptPresenter(Application application) {
		super(application);
		addHandler(Events.NEW_VERSION_AVAILABLE, this);
	}

	@Override
	public void newVersionAvailable(String currentVersion, String newVersion) {
		view.show();
	}

	public void acceptUpdateButtonClicked() {
		view.disableButtons();
		application.pwa.clearCache(() ->application.reload());
	}

	@Override
	public void userWantsToDismissPopup() {
		view.hide();
	}
}
