package superstartrek.client.activities.intro;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.pwa.ApplicationUpdateCheckHandler;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.GameStartedEvent;

public class IntroPresenter extends BasePresenter implements ApplicationUpdateCheckHandler, GamePhaseHandler, ValueChangeHandler<String>{

	public IntroPresenter(Application application) {
		super(application);
		application.events.addHandler(GameStartedEvent.TYPE, this);
		application.events.addHandler(ApplicationUpdateEvent.TYPE, this);
		History.addValueChangeHandler(this);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public IntroView getView() {
		return super.getView();
	}
	
	@Override
	public void onGameStarted(GameStartedEvent evt) {
		getView().show();
		History.newItem("intro");
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		if ("intro".equals(event.getValue())) {
			getView().show();
		} else
			getView().hide();
	}
	
	@Override
	public void installedAppVersionIs(String version) {
		getView().showAppVersion(version);
	}

}
