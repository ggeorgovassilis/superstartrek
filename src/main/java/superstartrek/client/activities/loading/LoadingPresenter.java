package superstartrek.client.activities.loading;

import superstartrek.client.activities.BasePresenter;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.eventbus.Events;
import superstartrek.client.space.StarMap;

public class LoadingPresenter extends BasePresenter<LoadingScreen> implements GamePhaseHandler{

	public LoadingPresenter() {
		addHandler(Events.GAME_STARTED);
	}

	@Override
	public void onGameStarted(StarMap map) {
		view.hide();
	}

}
