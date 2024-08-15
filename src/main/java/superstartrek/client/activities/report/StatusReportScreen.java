package superstartrek.client.activities.report;

import superstartrek.client.activities.View;

public interface StatusReportScreen extends View<StatusReportPresenter>{

	void setProperty(String property, String value, boolean highlight);

	void setOverlay(String overlay, String status);

}