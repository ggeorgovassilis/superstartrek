package superstartrek.client.activities.settings;

import superstartrek.client.activities.ActivityChangedHandler;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.pwa.ApplicationLifecycleHandler;
import superstartrek.client.eventbus.Events;

public class SettingsPresenter extends BasePresenter<ISettingsScreen> implements ApplicationLifecycleHandler, ActivityChangedHandler{
	
	boolean updateButtonEnabled = true;
	
	public SettingsPresenter() {
		addHandler(Events.ACTIVITY_CHANGED, this);
		addHandler(Events.VERSION_CHECK_FAILED, this);
		addHandler(Events.VERSION_IS_CURRENT, this);
		addHandler(Events.NEW_VERSION_AVAILABLE, this);

	}
	
	@Override
	public void onActivityChanged(String activity) {
		if ("settings".equals(activity)) {
			view.selectUIScale(getApplication().getUIScale());
			view.selectTheme(getApplication().getUITheme());
			view.selectNavigationAlignment(getApplication().getNavigationElementAlignmentPreference());
			view.show();
		} else
			view.hide();
	}

	public void onUIScaleSettingClicked(String scale) {
		getApplication().setUIScale(scale);
	}
	
	public void onUIThemeSettingClicked(String theme) {
		getApplication().setUITheme(theme);
	}
	
	public void onNavigationAlignmentChanged(String alignment) {
		getApplication().setNavigationElementAlignmentPreference(alignment);
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
		getApplication().pwa.checkForNewVersion();
	}

	@Override
	public void installedAppVersionIs(String version) {
		setUpdateCheckButtonEnabled(true);
		view.showAppVersion(version);
	}

	@Override
	public void checkFailed() {
		setUpdateCheckButtonEnabled(true);
		getApplication().message("Update check failed","error");
	}
	
	@Override
	public void versionIsCurrent(String currentVersion) {
		setUpdateCheckButtonEnabled(true);
		getApplication().message("No updates found. Current build is "+currentVersion,"info");
	}
	
	@Override
	public void newVersionAvailable(String currentVersion, String newVersion) {
		setUpdateCheckButtonEnabled(true);
	}
	
}
