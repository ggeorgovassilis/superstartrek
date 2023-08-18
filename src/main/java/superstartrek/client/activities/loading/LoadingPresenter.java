package superstartrek.client.activities.loading;

import superstartrek.client.activities.BasePresenter;
import superstartrek.client.bus.Events;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.model.StarMap;

public class LoadingPresenter extends BasePresenter<LoadingScreen> implements GamePhaseHandler{

	public LoadingPresenter() {
		addHandler(Events.GAME_STARTED, this);
	}

	@Override
	public void onGameStarted(StarMap map) {
		view.hide();
	}

}
