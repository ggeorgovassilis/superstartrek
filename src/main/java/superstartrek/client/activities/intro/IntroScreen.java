package superstartrek.client.activities.intro;

import com.google.gwt.user.client.DOM;

import superstartrek.client.Resources;
import superstartrek.client.activities.BaseScreen;

public class IntroScreen extends BaseScreen{

	public IntroScreen(IntroPresenter presenter) {
		super(presenter, DOM.createDiv());
		getElement().setInnerHTML(Resources.INSTANCE.introScreen().getText());
		presenter.getApplication().page.add(this);
	}

}
