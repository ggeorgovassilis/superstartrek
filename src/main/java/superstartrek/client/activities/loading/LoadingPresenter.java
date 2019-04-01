package superstartrek.client.activities.loading;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.GameStartedEvent;

public class LoadingPresenter extends BasePresenter<LoadingActivity> implements GamePhaseHandler{

	public LoadingPresenter(Application application) {
		super(application);
		application.events.addHandler(GameStartedEvent.TYPE, this);
	}

	@Override
	public void onGameStarted(GameStartedEvent evt) {
		getView().hide();
	}

}
