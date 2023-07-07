package superstartrek.client.activities.settings;

import superstartrek.client.activities.View;

public interface ISettingsScreen extends View<SettingsPresenter>{

	void selectUIScale(String scale);
	void selectTheme(String theme);
	void selectNavigationAlignment(String alignment);
	void showAppVersion(String version);
	void disableUpdateCheckButton();
	void enableUpdateCheckButton();
}
