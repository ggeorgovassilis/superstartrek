package superstartrek.client.activities.intro;

import com.google.gwt.user.client.Event;

import superstartrek.client.activities.BaseScreen;
import superstartrek.client.screentemplates.ScreenTemplates;

public class IntroView extends BaseScreen<IntroPresenter>{
	
	public IntroView(IntroPresenter presenter) {
		super(presenter);
		ScreenTemplates templates = presenter.getApplication().getScreenTemplates();
		getElement().setInnerHTML(templates.introScreen().getText());
		addStyleName("intro-screen");
		sinkEvents(Event.ONCLICK);
	}

	@Override
	protected boolean alignsOnItsOwn() {
		return false;
	}

}
