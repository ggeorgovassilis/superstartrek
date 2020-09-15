package superstartrek.client.activities.settings;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.pwa.ApplicationLifecycleHandler;
import superstartrek.client.bus.Events;

public class SettingsPresenter extends BasePresenter<ISettingsScreen> implements ApplicationLifecycleHandler, ValueChangeHandler<String>{
	
	boolean updateButtonEnabled = true;
	
	public SettingsPresenter(Application application) {
		super(application);
		application.browserAPI.addHistoryListener(this);
		addHandler(Events.VERSION_CHECK_FAILED, this);
		addHandler(Events.VERSION_IS_CURRENT, this);
		addHandler(Events.NEW_VERSION_AVAILABLE, this);

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
	
	void setUpdateCheckButtonEnabled(boolean v) {
		updateButtonEnabled = v;
		if (v) view.enableUpdateCheckButton();
		else view.disableUpdateCheckButton();
	}
	
	public void onCheckForUpdatesButtonClicked() {
		if (!updateButtonEnabled)
			return;
		setUpdateCheckButtonEnabled(false);
		application.pwa.checkForNewVersion();
	}

	@Override
	public void installedAppVersionIs(String version, String timestamp) {
		setUpdateCheckButtonEnabled(true);
		view.showAppVersion(version +" "+timestamp);
	}

	@Override
	public void checkFailed() {
		setUpdateCheckButtonEnabled(true);
		application.message("Update check failed","error");
	}
	
	@Override
	public void versionIsCurrent() {
		view.enableUpdateCheckButton();
		application.message("No updates found","info");
	}
	
	@Override
	public void newVersionAvailable() {
	}
	
}
