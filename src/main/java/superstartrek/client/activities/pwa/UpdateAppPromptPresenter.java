package superstartrek.client.activities.pwa;

import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.PopupViewPresenter;
import superstartrek.client.bus.Events;

public class UpdateAppPromptPresenter extends BasePresenter<UpdateAppPromptView> implements PopupViewPresenter<UpdateAppPromptView>, ApplicationLifecycleHandler{

	public UpdateAppPromptPresenter() {
		addHandler(Events.NEW_VERSION_AVAILABLE, this);
	}

	@Override
	public void newVersionAvailable(String currentVersion, String newVersion) {
		view.show();
	}

	public void acceptUpdateButtonClicked() {
		view.hide();
		getApplication().pwa.clearCache(() ->getApplication().reload());
	}

	@Override
	public void cancelButtonClicked() {
		view.hide();
	}
}
