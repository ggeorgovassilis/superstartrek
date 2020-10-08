package superstartrek.client.activities.manual;

import superstartrek.client.activities.BaseScreen;
import superstartrek.client.screentemplates.ScreenTemplates;

public class ManualScreen extends BaseScreen<ManualPresenter>{

	public ManualScreen(ManualPresenter presenter) {
		super(presenter);
		addStyleName("manual-screen");
	}
	
	@Override
	protected void decorateScreen() {
		ScreenTemplates templates = presenter.getApplication().getScreenTemplates();
		String html = templates.manualScreen().getText(); 
		getElement().setInnerHTML(html);
	}
	
}
