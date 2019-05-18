package superstartrek.client.activities.pwa;

import superstartrek.client.bus.BaseHandler;

public interface ApplicationLifecycleHandler extends BaseHandler{

	default void newVersionAvailable(){};
	default void versionIsCurrent(){};
	default void checkFailed(){};
	default void appMustReload(){};
	default void installedAppVersionIs(String version, String timestamp) {};
	default void showInstallPrompt() {};
}
