package superstartrek.client.activities.settings;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;

public class SettingsPresenter extends BasePresenter<ISettingsScreen> implements ValueChangeHandler<String>{
	
	public SettingsPresenter(Application application) {
		super(application);
		application.browserAPI.addHistoryListener(this);
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		if ("settings".equals(event.getValue())) {
			view.selectUIScale(application.getUIScale());
			view.show();
		} else
			view.hide();
	}
	
	public void onUIScaleSettingClicked(String scale) {
		application.setUIScale(scale);
	}

}
