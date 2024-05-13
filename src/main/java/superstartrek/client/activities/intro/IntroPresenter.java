package superstartrek.client.activities.intro;

import static superstartrek.client.eventbus.Events.*;

import superstartrek.client.activities.ActivityChangedHandler;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.pwa.ApplicationLifecycleHandler;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.space.Constants;
import superstartrek.client.space.StarMap;

public class IntroPresenter extends BasePresenter<IntroScreen>
		implements ApplicationLifecycleHandler, GamePhaseHandler, ActivityChangedHandler {

	public IntroPresenter() {
		addHandler(GAME_STARTED);
		addHandler(INFORMING_OF_INSTALLED_VERSION);
		addHandler(ACTIVITY_CHANGED);
	}

	@Override
	public void onGameStarted(StarMap map) {
		//ugly hack, but: if it's after the start date, this is a save game reload and the intro can be skipped
		if (getStarMap().getStarDate()>Constants.START_DATE)
			return;
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
