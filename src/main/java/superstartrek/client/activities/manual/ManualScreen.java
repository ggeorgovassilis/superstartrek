package superstartrek.client.activities.manual;

import superstartrek.client.activities.BaseScreen;

public class ManualScreen extends BaseScreen<ManualActivity>{

	public ManualScreen(ManualPresenter presenter) {
		super(presenter);
		getElement().setInnerHTML(presenter.getApplication().getResources().manualScreen().getText());
		presenter.getApplication().page.add(this);
	}

}
