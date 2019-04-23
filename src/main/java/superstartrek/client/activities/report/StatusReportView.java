package superstartrek.client.activities.report;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTMLPanel;

import superstartrek.client.activities.BaseScreen;
import superstartrek.client.activities.CSS;

public class StatusReportView extends BaseScreen<StatusReportPresenter>{

	@Override
	protected HTMLPanel createWidgetImplementation() {
		return new HTMLPanel(presenter.getApplication().getResources().statusReport().getText());
	}
	
	public StatusReportView(StatusReportPresenter p) {
		super(p);
	}
	
	public void setProperty(String property, String value) {
		Element e = DOM.getElementById(property);
		e.setInnerText(value);
	}
	
	public void setOverlay(String overlay, String status) {
		CSS.querySelectorAll("#enterprise-schematics ."+overlay).getItem(0).setClassName(overlay+" overlay "+status);
	}

}
