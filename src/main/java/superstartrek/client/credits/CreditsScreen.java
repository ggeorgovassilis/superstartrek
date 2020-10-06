package superstartrek.client.credits;

import superstartrek.client.activities.BaseScreen;

public class CreditsScreen extends BaseScreen<CreditsPresenter>{
	
	public CreditsScreen(CreditsPresenter presenter) {
		super(presenter);
		addStyleName("credits-screen");
	}

	@Override
	protected void decorateScreen() {
		getElement().setInnerHTML(presenter.getApplication().getScreenTemplates().creditsScreen().getText());
	}
	
}
