package superstartrek.client.activities.manual;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import superstartrek.client.activities.BaseScreen;

public class ManualScreen extends BaseScreen<ManualActivity>{

	public ManualScreen(ManualPresenter presenter) {
		super(presenter);
	}
	
	@Override
	protected Widget createWidgetImplementation() {
		return new HTMLPanel(presenter.getApplication().getResources().manualScreen().getText());
	}
	
	
	

}
