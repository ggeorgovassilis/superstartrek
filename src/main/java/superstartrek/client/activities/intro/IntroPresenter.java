package superstartrek.client.activities.intro;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.loading.GameStartedEvent;
import superstartrek.client.activities.loading.GameStartedHandler;

public class IntroPresenter extends BasePresenter<IntroActivity> implements GameStartedHandler{

	public IntroPresenter(Application application) {
		super(application);
		application.events.addHandler(GameStartedEvent.TYPE, this);
		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				if ("intro".equals(event.getValue())) {
					getView().show();
				} else
					getView().hide();
			}
		});
	}
	
	@Override
	public void onGameStared(GameStartedEvent evt) {
		History.newItem("intro");
	}


}
