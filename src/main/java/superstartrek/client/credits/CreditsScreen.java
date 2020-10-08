package superstartrek.client.credits;

import superstartrek.client.activities.BaseScreen;
import superstartrek.client.screentemplates.ScreenTemplates;

public class CreditsScreen extends BaseScreen<CreditsPresenter>{
	
	public CreditsScreen(CreditsPresenter presenter) {
		super(presenter);
		addStyleName("credits-screen");
	}

	@Override
	protected void decorateScreen() {
		ScreenTemplates screenTemplates = presenter.getApplication().getScreenTemplates();
		getElement().setInnerHTML(screenTemplates.creditsScreen().getText());
	}
	
}
