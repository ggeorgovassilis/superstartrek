package superstartrek.client.activities.browserdiagnostics;

import com.google.gwt.dom.client.Element;

import superstartrek.client.activities.BaseScreen;
import superstartrek.client.screentemplates.ScreenTemplates;
import superstartrek.client.screentemplates.ScreenTemplates.TemplateNames;

public class BrowserDiagnosticsScreenImpl extends BaseScreen<BrowserDiagnosticsPresenter> implements BrowserDiagnosticsScreen{

	
	public BrowserDiagnosticsScreenImpl(BrowserDiagnosticsPresenter p) {
		super(p);
	}
	
	@Override
	protected void decorateScreen(ScreenTemplates templates, Element element) {
		super.decorateScreen(templates, element);
		element.setInnerHTML(templates.getTemplateFor(TemplateNames.browserDiagnostics));

	}

	@Override
	public void showLogs(String logs) {
		getElementById("browser-diagnostics").setInnerText(logs);
	}
	

}
