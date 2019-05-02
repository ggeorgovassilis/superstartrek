package superstartrek.client.activities.pwa;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.PopupViewPresenter;

public class UpdateAppPromptPresenter extends BasePresenter<UpdateAppPromptView> implements PopupViewPresenter<UpdateAppPromptView>, ApplicationLifecycleHandler{

	public UpdateAppPromptPresenter(Application application) {
		super(application);
		addHandler(ApplicationLifecycleEvent.TYPE, this);
	}

	@Override
	public void newVersionAvailable() {
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
