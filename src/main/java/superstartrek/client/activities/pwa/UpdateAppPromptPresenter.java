package superstartrek.client.activities.pwa;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;

public class UpdateAppPromptPresenter extends BasePresenter<UpdateAppPromptView> implements ApplicationUpdateCheckHandler{

	PWA pwa;
	
	public UpdateAppPromptPresenter(Application application) {
		super(application);
		application.events.addHandler(ApplicationUpdateEvent.TYPE, this);
		pwa = new PWA(application);
		pwa.run();
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
		((UpdateAppPromptView)getView()).disableButtons();
		PWA.clearCache(new ScheduledCommand() {
			
			@Override
			public void execute() {
				application.reload();
			}
		});
	}

	public void dismissButtonClicked() {
		getView().hide();
	}
}
