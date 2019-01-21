package superstartrek.client.activities.intro;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.loading.GameStartedEvent;
import superstartrek.client.activities.loading.GameStartedHandler;

public class IntroPresenter extends BasePresenter implements GameStartedHandler{

	public IntroPresenter(Application application) {
		super(application);
		application.events.addHandler(GameStartedEvent.TYPE, this);
	}
	
	@Override
	public void onGameStared(GameStartedEvent evt) {
		getScreen().show();
	}


}
