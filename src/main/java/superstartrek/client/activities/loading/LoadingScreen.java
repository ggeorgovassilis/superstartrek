package superstartrek.client.activities.loading;

import com.google.gwt.user.client.DOM;

import superstartrek.client.Application;
import superstartrek.client.activities.BaseScreen;
import superstartrek.client.activities.Presenter;

public class LoadingScreen extends BaseScreen{

	public LoadingScreen(LoadingPresenter presenter) {
		super(presenter, DOM.getElementById("screen-loading"));
	}

}
