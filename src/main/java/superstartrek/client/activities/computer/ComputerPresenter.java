package superstartrek.client.activities.computer;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.glasspanel.GlassPanelEvent;
import superstartrek.client.activities.glasspanel.GlassPanelEvent.Action;

public class ComputerPresenter extends BasePresenter<ComputerActivity> implements ComputerHandler{

	public ComputerPresenter(Application application) {
		super(application);
		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				if ("computer".equals(event.getValue()))
					showScreen();
				else
					hideScreen();
			}
		});
		application.events.addHandler(ComputerEvent.TYPE, this);
	}

	@Override
	public void showScreen() {
		application.events.fireEvent(new GlassPanelEvent(Action.hide));
		getView().show();
	}

	@Override
	public void hideScreen() {
		getView().hide();
	}

}
