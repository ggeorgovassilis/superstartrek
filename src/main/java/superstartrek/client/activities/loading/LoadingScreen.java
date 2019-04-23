package superstartrek.client.activities.loading;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTMLPanel;

import superstartrek.client.activities.BaseScreen;

public class LoadingScreen extends BaseScreen<LoadingPresenter> {

	@Override
	protected HTMLPanel createWidgetImplementation() {
		Element e = DOM.getElementById("screen-loading");
		return HTMLPanel.wrap(e);
	}

	public LoadingScreen(LoadingPresenter presenter) {
		super(presenter);
	}

}
