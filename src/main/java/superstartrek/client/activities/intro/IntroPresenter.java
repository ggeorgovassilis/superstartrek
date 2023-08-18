package superstartrek.client.activities.intro;

import superstartrek.client.activities.ActivityChangedHandler;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.pwa.ApplicationLifecycleHandler;
import superstartrek.client.bus.Events;

import static superstartrek.client.bus.Events.*;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.model.StarMap;

public class IntroPresenter extends BasePresenter<IntroView>
		implements ApplicationLifecycleHandler, GamePhaseHandler, ActivityChangedHandler {

	public IntroPresenter() {
		addHandler(GAME_STARTED, this);
		addHandler(INFORMING_OF_INSTALLED_VERSION, this);
		addHandler(Events.ACTIVITY_CHANGED, this);
	}

	@Override
	public void onGameStarted(StarMap map) {
		view.show();
		getApplication().browserAPI.postHistoryChange("intro");
	}

	@Override
	public void onActivityChanged(String activity) {
		if ("intro".equals(activity)) {
			view.show();
		} else
			view.hide();
	}
}
