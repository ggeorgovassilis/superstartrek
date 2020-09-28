package superstartrek.client.activities.pwa;

import superstartrek.client.bus.EventHandler;

public interface ApplicationLifecycleHandler extends EventHandler{

	default void newVersionAvailable(){};
	default void versionIsCurrent(){};
	default void checkFailed(){};
	default void appMustReload(){};
	default void installedAppVersionIs(String version) {};
	default void showInstallPrompt() {};
}
