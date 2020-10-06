package superstartrek.client.activities.report;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import superstartrek.client.activities.BaseScreen;
import superstartrek.client.activities.CSS;

public class StatusReportView extends BaseScreen<StatusReportPresenter>{

	@Override
	protected void createWidgetImplementation() {
		super.createWidgetImplementation();
		Element e = getElement();
		e.setId("screen-statusreport");
		e.setInnerHTML(presenter.getApplication().getScreenTemplates().statusReport().getText());
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
