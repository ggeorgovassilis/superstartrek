package superstartrek.client.credits;

import com.google.gwt.dom.client.Element;

import superstartrek.client.activities.BaseScreen;
import superstartrek.client.screentemplates.ScreenTemplates;

public class CreditsScreen extends BaseScreen<CreditsPresenter>{
	
	public CreditsScreen(CreditsPresenter presenter) {
		super(presenter);
		addStyleName("credits-screen");
	}

	@Override
	protected void decorateScreen(ScreenTemplates templates, Element element) {
		element.setInnerHTML(templates.creditsScreen().getText());
	}
	
}
