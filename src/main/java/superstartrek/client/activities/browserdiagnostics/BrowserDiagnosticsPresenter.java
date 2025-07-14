package superstartrek.client.activities.browserdiagnostics;

import com.google.gwt.core.client.GWT;

import superstartrek.client.activities.ActivityChangedHandler;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.eventbus.Events;

public class BrowserDiagnosticsPresenter extends BasePresenter<BrowserDiagnosticsScreen> implements ActivityChangedHandler {
	
	String lastLogs = "";
	
	String generateLogs() {
		return getApplication().browserAPI.getBrowserReport();
	}

	public void updateView() {
		lastLogs = generateLogs();
		view.showLogs(lastLogs);
	}

	public BrowserDiagnosticsPresenter() {
		addHandler(Events.ACTIVITY_CHANGED);
	}

	@Override
	public void onActivityChanged(String activity) {
		if ("browser-diagnostics".equals(activity)) {
			updateView();
			view.show();
		} else
			view.hide();
	}
	

}
