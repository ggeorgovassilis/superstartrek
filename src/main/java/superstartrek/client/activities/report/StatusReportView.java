package superstartrek.client.activities.report;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import superstartrek.client.activities.BaseScreen;
import superstartrek.client.activities.CSS;
import superstartrek.client.screentemplates.ScreenTemplates;

public class StatusReportView extends BaseScreen<StatusReportPresenter>{

	
	@Override
	protected void decorateScreen() {
		Element e = getElement();
		e.setId("screen-statusreport");
		ScreenTemplates screenTemplates = presenter.getApplication().getScreenTemplates();
		e.setInnerHTML(screenTemplates.statusReport().getText());
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
	
	@Override
	protected boolean alignsOnItsOwn() {
		return false;
	}

}
