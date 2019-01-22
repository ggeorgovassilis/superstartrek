package superstartrek.client.activities.loading;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTMLPanel;

import superstartrek.client.Application;
import superstartrek.client.activities.BaseActivity;
import superstartrek.client.activities.BaseScreen;
import superstartrek.client.activities.Presenter;

public class LoadingScreen extends BaseScreen {

	@Override
	protected HTMLPanel createPanel() {
		Element e = DOM.getElementById("screen-loading");
		return HTMLPanel.wrap(e);
	}

	public LoadingScreen(LoadingPresenter presenter) {
		super(presenter);
	}

}
