package superstartrek.client.activities.intro;

import superstartrek.client.activities.BaseScreen;

public class IntroView extends BaseScreen<IntroActivity>{

	public IntroView(IntroPresenter presenter) {
		super(presenter);
		getElement().setInnerHTML(presenter.getApplication().getResources().introScreen().getText());
	}

}
