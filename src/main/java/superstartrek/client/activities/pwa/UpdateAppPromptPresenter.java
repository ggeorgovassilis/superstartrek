package superstartrek.client.activities.pwa;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.PopupViewPresenter;

public class UpdateAppPromptPresenter extends BasePresenter<UpdateAppPromptView> implements PopupViewPresenter<UpdateAppPromptView>, ApplicationUpdateCheckHandler{

	PWA pwa;
	
	public UpdateAppPromptPresenter(Application application) {
		super(application);
		addHandler(ApplicationUpdateEvent.TYPE, this);
		pwa = new PWA(application);
		pwa.run();
	}

	@Override
	public void newVersionAvailable() {
		view.show();
	}

	public void acceptUpdateButtonClicked() {
		view.disableButtons();
		PWA.clearCache(new ScheduledCommand() {
			
			@Override
			public void execute() {
				application.reload();
			}
		});
	}

	@Override
	public void userWantsToDismissPopup() {
		view.hide();
	}
}
