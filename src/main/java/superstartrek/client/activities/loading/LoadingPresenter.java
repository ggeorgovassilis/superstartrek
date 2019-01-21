package superstartrek.client.activities.loading;

import com.google.gwt.core.client.GWT;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;

public class LoadingPresenter extends BasePresenter implements GameStartedHandler{

	public LoadingPresenter(Application application) {
		super(application);
		application.events.addHandler(GameStartedEvent.TYPE, this);
	}

	public void onGameStared(GameStartedEvent evt) {
		GWT.log("onGameStarted");
		getScreen().hide();
	}

}
