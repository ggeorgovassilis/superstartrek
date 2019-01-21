package superstartrek.client.activities.loading;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

import superstartrek.client.Application;
import superstartrek.client.activities.BaseScreen;
import superstartrek.client.activities.Presenter;

public class LoadingScreen extends BaseScreen{
	
	@Override
	protected Element createRootElement() {
		return DOM.getElementById("screen-loading");
	}

	public LoadingScreen(LoadingPresenter presenter) {
		super(presenter);
	}

}
