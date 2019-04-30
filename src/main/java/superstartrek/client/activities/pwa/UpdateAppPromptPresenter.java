package superstartrek.client.activities.pwa;

import java.util.logging.Logger;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.PopupViewPresenter;

public class UpdateAppPromptPresenter extends BasePresenter<UpdateAppPromptView> implements PopupViewPresenter<UpdateAppPromptView>, ApplicationLifecycleHandler{

	private static Logger log = Logger.getLogger("");

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
		application.pwa.clearCache(new ScheduledCommand() {
			
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
