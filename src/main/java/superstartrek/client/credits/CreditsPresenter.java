package superstartrek.client.credits;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.pwa.ApplicationLifecycleHandler;
import superstartrek.client.bus.Events;

import static superstartrek.client.bus.Events.*;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.model.StarMap;

public class CreditsPresenter extends BasePresenter<CreditsScreen> implements ApplicationLifecycleHandler, GamePhaseHandler, ValueChangeHandler<String>{


	public CreditsPresenter(Application application) {
		super(application);
		application.browserAPI.addHistoryListener(this);
	}
	
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		if ("credits".equals(event.getValue())) {
			view.show();
		} else
			view.hide();
	}
	
}
