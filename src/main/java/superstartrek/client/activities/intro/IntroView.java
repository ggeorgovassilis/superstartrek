package superstartrek.client.activities.intro;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

import superstartrek.client.activities.BaseScreen;
import superstartrek.client.screentemplates.ScreenTemplates;
import superstartrek.client.utils.HtmlWidget;

public class IntroView extends BaseScreen<IntroPresenter>{
	
	public IntroView(IntroPresenter presenter) {
		super(presenter);
		ScreenTemplates templates = presenter.getApplication().getScreenTemplates();
		getElement().setInnerHTML(templates.introScreen().getText());
		addStyleName("intro-screen");
		getWidget().sinkEvents(Event.ONCLICK);
	}

	@Override
	protected Widget createWidgetImplementation() {
		return new HtmlWidget(DOM.createDiv());
	}
	
	@Override
	protected boolean alignsOnItsOwn() {
		return false;
	}
	
}
