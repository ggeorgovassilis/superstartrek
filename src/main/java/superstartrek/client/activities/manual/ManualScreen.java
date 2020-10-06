package superstartrek.client.activities.manual;

import superstartrek.client.activities.BaseScreen;

public class ManualScreen extends BaseScreen<ManualPresenter>{

	public ManualScreen(ManualPresenter presenter) {
		super(presenter);
		addStyleName("manual-screen");
	}
	
	@Override
	protected void decorateScreen() {
		getElement().setInnerHTML(presenter.getApplication().getScreenTemplates().manualScreen().getText());
	}
	
}
