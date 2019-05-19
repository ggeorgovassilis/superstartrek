package superstartrek.client.activities.intro;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.pwa.ApplicationLifecycleHandler;
import static superstartrek.client.bus.Events.*;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.model.StarMap;

public class IntroPresenter extends BasePresenter<IntroView> implements ApplicationLifecycleHandler, GamePhaseHandler, ValueChangeHandler<String>{

	public IntroPresenter(Application application) {
		super(application);
		addHandler(GAME_STARTED, this);
		addHandler(INFORMING_OF_INSTALLED_VERSION, this);
		application.browserAPI.addHistoryListener(this);
	}
	
	@Override
	public void onGameStarted(StarMap map) {
		view.show();
		application.browserAPI.postHistoryChange("intro");
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		if ("intro".equals(event.getValue())) {
			view.show();
		} else
			view.hide();
	}
	
	@Override
	public void installedAppVersionIs(String version, String timestamp) {
		view.showAppVersion(version +" "+timestamp);
	}

}
