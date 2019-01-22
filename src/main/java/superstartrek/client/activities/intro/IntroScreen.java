package superstartrek.client.activities.intro;

import com.google.gwt.user.client.DOM;

import superstartrek.client.Resources;
import superstartrek.client.activities.BaseActivity;
import superstartrek.client.activities.BaseScreen;

public class IntroScreen extends BaseScreen{

	public IntroScreen(IntroPresenter presenter) {
		super(presenter);
		getElement().setInnerHTML(Resources.INSTANCE.introScreen().getText());
		presenter.getApplication().page.add(this);
	}

}
