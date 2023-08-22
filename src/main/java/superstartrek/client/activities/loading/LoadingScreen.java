package superstartrek.client.activities.loading;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import superstartrek.client.activities.BaseScreen;

public class LoadingScreen extends BaseScreen<LoadingPresenter> {

	@Override
	protected void createWidgetImplementation() {
		Element e = getElementById("screen-loading");
		setElement(e);
	}

	public LoadingScreen(LoadingPresenter presenter) {
		super(presenter);
	}

}
