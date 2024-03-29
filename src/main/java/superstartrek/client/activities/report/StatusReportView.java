package superstartrek.client.activities.report;

import com.google.gwt.dom.client.Element;
import superstartrek.client.activities.BaseScreen;
import superstartrek.client.screentemplates.ScreenTemplates;
import superstartrek.client.utils.CSS;

public class StatusReportView extends BaseScreen<StatusReportPresenter>{

	
	@Override
	protected void decorateScreen(ScreenTemplates templates, Element element) {
		element.setId("screen-statusreport");
		element.setInnerHTML(templates.statusReport().getText());
	}

	public StatusReportView(StatusReportPresenter p) {
		super(p);
	}
	
	public void setProperty(String property, String value, boolean highlight) {
		Element e = getElementById(property);
		e.setInnerText(value);
		e.removeClassName("highlight");
		if (highlight)
			e.addClassName("highlight");
	}
	
	public void setOverlay(String overlay, String status) {
		CSS.querySelectorAll("#enterprise-schematics ."+overlay).getItem(0).setClassName(overlay+" overlay "+status);
	}
	
	@Override
	protected boolean alignsOnItsOwn() {
		return false;
	}

}
