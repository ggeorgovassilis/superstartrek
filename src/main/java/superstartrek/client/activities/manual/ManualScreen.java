package superstartrek.client.activities.manual;

import com.google.gwt.dom.client.Element;

import superstartrek.client.activities.BaseScreen;
import superstartrek.client.screentemplates.ScreenTemplates;

public class ManualScreen extends BaseScreen<ManualPresenter>{

	public ManualScreen(ManualPresenter presenter) {
		super(presenter);
		addStyleName("manual-screen");
	}
	
	@Override
	protected void decorateScreen(ScreenTemplates templates, Element element) {
		String html = templates.manualScreen().getText(); 
		element.setInnerHTML(html);
	}
	
}
