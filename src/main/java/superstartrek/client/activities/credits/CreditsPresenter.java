
package superstartrek.client.activities.credits;

import superstartrek.client.activities.ActivityChangedHandler;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.pwa.ApplicationLifecycleHandler;
import superstartrek.client.bus.Events;
import superstartrek.client.control.GamePhaseHandler;

public class CreditsPresenter extends BasePresenter<CreditsScreen>
		implements ApplicationLifecycleHandler, GamePhaseHandler, ActivityChangedHandler {

	public CreditsPresenter() {
		addHandler(Events.ACTIVITY_CHANGED, this);
	}

	@Override
	public void onActivityChanged(String activity) {
		if ("credits".equals(activity)) {
			view.show();
		} else
			view.hide();
	}


}
