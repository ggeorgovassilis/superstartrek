package superstartrek.client.activities.manual;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

import superstartrek.client.activities.BaseScreen;
import superstartrek.client.utils.HtmlWidget;

public class ManualScreen extends BaseScreen<ManualPresenter>{

	public ManualScreen(ManualPresenter presenter) {
		super(presenter);
		addStyleName("manual-screen");
	}
	
	@Override
	protected Widget createWidgetImplementation() {
		return new HtmlWidget(DOM.createDiv(),presenter.getApplication().getScreenTemplates().manualScreen().getText());
	}

}
