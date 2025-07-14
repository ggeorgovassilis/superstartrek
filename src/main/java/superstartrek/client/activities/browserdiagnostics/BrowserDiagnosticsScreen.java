package superstartrek.client.activities.browserdiagnostics;

import superstartrek.client.activities.View;

public interface BrowserDiagnosticsScreen extends View<BrowserDiagnosticsPresenter>{

	void showLogs(String logs);
	
}