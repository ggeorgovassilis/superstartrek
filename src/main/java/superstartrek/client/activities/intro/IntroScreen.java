package superstartrek.client.activities.intro;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;

import superstartrek.client.activities.BaseScreen;
import superstartrek.client.screentemplates.ScreenTemplates;

public class IntroScreen extends BaseScreen<IntroPresenter>{
	
	public IntroScreen(IntroPresenter presenter) {
		super(presenter);
	}
	
	@Override
	protected void decorateScreen(ScreenTemplates templates, Element element) {
		element.setInnerHTML(templates.introScreen().getText());
		addStyleName("intro-screen");
		sinkEvents(Event.ONCLICK);
	}

	@Override
	protected boolean alignsOnItsOwn() {
		return false;
	}

}
