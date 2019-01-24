package superstartrek.client.activities.loading;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;

public class LoadingPresenter extends BasePresenter<LoadingActivity> implements GameStartedHandler{

	public LoadingPresenter(Application application) {
		super(application);
		application.events.addHandler(GameStartedEvent.TYPE, this);
	}

	@Override
	public void onGameStared(GameStartedEvent evt) {
		getView().hide();
	}

}
