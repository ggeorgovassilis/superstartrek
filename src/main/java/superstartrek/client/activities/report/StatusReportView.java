package superstartrek.client.activities.report;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTMLPanel;

import superstartrek.client.Resources;
import superstartrek.client.activities.BaseScreen;
import superstartrek.client.activities.Presenter;

public class StatusReportView extends BaseScreen<StatusReportActivity>{

	@Override
	protected HTMLPanel createPanel() {
		return new HTMLPanel(Resources.INSTANCE.statusReport().getText());
	}
	
	public StatusReportView(Presenter<StatusReportActivity> p) {
		super(p);
	}
	
	public void setProperty(String property, String value) {
		Element e = DOM.getElementById(property);
		e.setInnerText(value);
	}

}
