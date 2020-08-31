package superstartrek.client.activities.report;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import superstartrek.client.activities.BaseScreen;
import superstartrek.client.activities.CSS;
import superstartrek.client.utils.HtmlWidget;

public class StatusReportView extends BaseScreen<StatusReportPresenter>{

	@Override
	protected HtmlWidget createWidgetImplementation() {
		Element e = DOM.createDiv();
		e.setId("screen-statusreport");
		return new HtmlWidget(e, presenter.getApplication().getResources().statusReport().getText());
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
