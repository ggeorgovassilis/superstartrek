package superstartrek.client.activities.intro;

import superstartrek.client.Resources;
import superstartrek.client.activities.BaseScreen;

public class IntroView extends BaseScreen<IntroActivity>{

	public IntroView(IntroPresenter presenter) {
		super(presenter);
		getElement().setInnerHTML(Resources.INSTANCE.introScreen().getText());
		presenter.getApplication().page.add(this);
	}

}
