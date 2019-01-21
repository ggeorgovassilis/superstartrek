package superstartrek.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTMLPanel;

import superstartrek.client.activities.intro.IntroPresenter;
import superstartrek.client.activities.intro.IntroScreen;
import superstartrek.client.activities.loading.GameStartedEvent;
import superstartrek.client.activities.loading.LoadingPresenter;
import superstartrek.client.activities.loading.LoadingScreen;

public class Application implements EntryPoint{

	public EventBus events;
	public LoadingScreen loadingScreen;
	public LoadingPresenter loadingPresenter;
	public IntroPresenter introPresenter;
	public IntroScreen introScreen;
	public HTMLPanel page;
	
	protected void setupScreens() {
		loadingPresenter = new LoadingPresenter(this);
		loadingScreen = new LoadingScreen(loadingPresenter);
		
		introPresenter = new IntroPresenter(this);
		introScreen = new IntroScreen(introPresenter);
	}
	
	public void onModuleLoad() {
		page = HTMLPanel.wrap(DOM.getElementById("page"));
		GWT.log("Application started");
		events = GWT.create(SimpleEventBus.class);
		setupScreens();
		events.fireEvent(new GameStartedEvent());
	}

}
