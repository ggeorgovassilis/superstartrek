package superstartrek.client.activities.manual;

import com.google.gwt.user.client.DOM;

import superstartrek.client.Resources;
import superstartrek.client.activities.BaseScreen;
import superstartrek.client.activities.Presenter;

public class ManualScreen extends BaseScreen{

	public ManualScreen(Presenter presenter) {
		super(presenter);
		getElement().setInnerHTML(Resources.INSTANCE.manualScreen().getText());
		presenter.getApplication().page.add(this);
	}

}
