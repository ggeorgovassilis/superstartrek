package superstartrek.client.activities.settings;

import com.google.gwt.core.client.GWT;
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
			view.selectTheme(application.getUITheme());
			view.show();
		} else
			view.hide();
	}
	
	public void onUIScaleSettingClicked(String scale) {
		application.setUIScale(scale);
	}
	
	public void onUIThemeSettingClicked(String theme) {
		application.setUITheme(theme);
	}
	
	public void onCheckForUpdatesButtonClicked() {
		application.pwa.checkForNewVersion();
	}

}
