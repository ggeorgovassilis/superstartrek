package superstartrek.client.activities.intro;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.pwa.ApplicationUpdateCheckHandler;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.GameStartedEvent;

public class IntroPresenter extends BasePresenter<IntroView> implements ApplicationUpdateCheckHandler, GamePhaseHandler, ValueChangeHandler<String>{

	public IntroPresenter(Application application) {
		super(application);
		addHandler(GameStartedEvent.TYPE, this);
		addHandler(ApplicationUpdateEvent.TYPE, this);
		application.addHistoryListener(this);
	}
	
	@Override
	public void onGameStarted(GameStartedEvent evt) {
		view.show();
		History.newItem("intro");
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		if ("intro".equals(event.getValue())) {
			view.show();
		} else
			view.hide();
	}
	
	@Override
	public void installedAppVersionIs(String version) {
		view.showAppVersion(version);
	}

}
