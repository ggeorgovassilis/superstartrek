package superstartrek.client.activities.pwa;

import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.popup.PopupViewPresenter;
import superstartrek.client.eventbus.Events;
import superstartrek.client.uihandler.InteractionHandler;

public class UpdateAppPromptPresenter extends BasePresenter<UpdateAppPromptView>
		implements PopupViewPresenter<UpdateAppPromptView>, ApplicationLifecycleHandler, InteractionHandler {

	public UpdateAppPromptPresenter() {
		addHandler(Events.NEW_VERSION_AVAILABLE);
		addHandler(Events.INTERACTION);
	}

	@Override
	public void newVersionAvailable(String currentVersion, String newVersion) {
		view.show();
	}

	public void acceptUpdateButtonClicked() {
		view.hide();
		getApplication().pwa.clearCache(() -> getApplication().reload());
	}

	@Override
	public void cancelButtonClicked() {
		view.hide();
	}

	@Override
	public void onUiInteraction(String tag) {
		switch (tag) {
		case "update-yes":
			acceptUpdateButtonClicked();
			break;
		case "update-no":
			cancelButtonClicked();
			break;
		}
	}
}
