package superstartrek.client.activities.manual;

import superstartrek.client.Resources;
import superstartrek.client.activities.BaseScreen;

public class ManualScreen extends BaseScreen<ManualActivity>{

	public ManualScreen(ManualPresenter presenter) {
		super(presenter);
		getElement().setInnerHTML(Resources.INSTANCE.manualScreen().getText());
		presenter.getApplication().page.add(this);
	}

}
