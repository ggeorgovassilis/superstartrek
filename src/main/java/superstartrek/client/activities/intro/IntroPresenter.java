package superstartrek.client.activities.intro;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.GameStartedEvent;

public class IntroPresenter extends BasePresenter<IntroActivity> implements GamePhaseHandler, ValueChangeHandler<String>{

	public IntroPresenter(Application application) {
		super(application);
		application.events.addHandler(GameStartedEvent.TYPE, this);
		History.addValueChangeHandler(this);
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


}
