package superstartrek.client.pwa;

import com.google.gwt.event.shared.EventHandler;

public interface ApplicationUpdateCheckHandler extends EventHandler{

	void newVersionAvailable();
	void versionIsCurrent();
	void checkFailed();
}
