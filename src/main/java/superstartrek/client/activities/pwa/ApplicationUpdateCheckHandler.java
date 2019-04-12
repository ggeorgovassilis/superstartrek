package superstartrek.client.activities.pwa;

import com.google.gwt.event.shared.EventHandler;

public interface ApplicationUpdateCheckHandler extends EventHandler{

	default void newVersionAvailable(){};
	default void versionIsCurrent(){};
	default void checkFailed(){};
	default void appMustReload(){};
}
