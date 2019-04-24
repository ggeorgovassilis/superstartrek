package superstartrek.client.activities.loading;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTMLPanel;

import superstartrek.client.activities.BaseScreen;
import superstartrek.client.utils.HtmlWidget;

public class LoadingScreen extends BaseScreen<LoadingPresenter> {

	@Override
	protected HtmlWidget createWidgetImplementation() {
		Element e = DOM.getElementById("screen-loading");
		return new HtmlWidget(e, false);
	}

	public LoadingScreen(LoadingPresenter presenter) {
		super(presenter);
	}

}
