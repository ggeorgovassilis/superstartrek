package superstartrek.client.pwa;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;

public class UpdateAppPromptPresenter extends BasePresenter<UpdateAppActivity> implements ApplicationUpdateCheckHandler{

	public UpdateAppPromptPresenter(Application application) {
		super(application);
		application.events.addHandler(ApplicationUpdateEvent.TYPE, this);
	}

	@Override
	public void newVersionAvailable() {
		getView().show();
	}

	@Override
	public void versionIsCurrent() {
	}

	@Override
	public void checkFailed() {
	}
	
	public void acceptUpdateButtonClicked() {
		application.pwa.clearCache();
	}

	public void dismissButtonClicked() {
		getView().hide();
	}
}
