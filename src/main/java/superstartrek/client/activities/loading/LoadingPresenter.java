package superstartrek.client.activities.loading;

import com.google.gwt.event.shared.HandlerRegistration;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.GameStartedEvent;

public class LoadingPresenter extends BasePresenter<LoadingActivity> implements GamePhaseHandler{

	HandlerRegistration handlerRegistration;
	
	public LoadingPresenter(Application application) {
		super(application);
		handlerRegistration = application.events.addHandler(GameStartedEvent.TYPE, this);
	}

	@Override
	public void onGameStarted(GameStartedEvent evt) {
		getView().hide();
		//we won't need this presenter or its view after the initial event
		handlerRegistration.removeHandler();
		handlerRegistration = null;
	}

}
