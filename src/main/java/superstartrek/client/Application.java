package superstartrek.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HTMLPanel;

import superstartrek.client.activities.computer.ComputerPresenter;
import superstartrek.client.activities.computer.ComputerView;
import superstartrek.client.activities.computer.TurnEndedEvent;
import superstartrek.client.activities.computer.TurnStartedEvent;
import superstartrek.client.activities.computer.srs.SRSPresenter;
import superstartrek.client.activities.computer.srs.SRSView;
import superstartrek.client.activities.glasspanel.GlassPanelView;
import superstartrek.client.activities.glasspanel.GlassPanelPresenter;
import superstartrek.client.activities.intro.IntroPresenter;
import superstartrek.client.activities.intro.IntroView;
import superstartrek.client.activities.loading.GameStartedEvent;
import superstartrek.client.activities.loading.LoadingPresenter;
import superstartrek.client.activities.loading.LoadingScreen;
import superstartrek.client.activities.manual.ManualPresenter;
import superstartrek.client.activities.manual.ManualScreen;
import superstartrek.client.activities.messages.MessagesView;
import superstartrek.client.activities.messages.MessageEvent;
import superstartrek.client.activities.messages.MessagesPresenter;
import superstartrek.client.activities.sector.scan.ScanSectorPresenter;
import superstartrek.client.activities.sector.scan.ScanSectorView;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Setup;
import superstartrek.client.model.StarMap;

public class Application implements EntryPoint{

	public EventBus events;
	public LoadingPresenter loadingPresenter;
	public ComputerPresenter computerPresenter;
	public IntroPresenter introPresenter;
	public GlassPanelPresenter glassPanelPresenter;
	public ManualPresenter manualPresenter;
	public ScanSectorPresenter scanSectorPresenter;
	public MessagesPresenter messagesPresenter;
	public HTMLPanel page;
	public StarMap starMap;
	
	protected void setupHistory() {
		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
			}
		});
	}
	
	protected void setupStarMap() {
		Setup setup = new Setup(this);
		starMap = setup.createNewMap();
	}
	
	protected void setupScreens() {
		loadingPresenter = new LoadingPresenter(this);
		new LoadingScreen(loadingPresenter);
		
		introPresenter = new IntroPresenter(this);
		new IntroView(introPresenter);
	
		manualPresenter = new ManualPresenter(this);
		new ManualScreen(manualPresenter);
		
		computerPresenter = new ComputerPresenter(this);
		new ComputerView(computerPresenter);
		
		glassPanelPresenter = new GlassPanelPresenter(this);
		new GlassPanelView(glassPanelPresenter);
		
		scanSectorPresenter = new ScanSectorPresenter(this);
		new ScanSectorView(scanSectorPresenter);
		
		messagesPresenter = new MessagesPresenter(this);
		new MessagesView(messagesPresenter);
		
	}
	
	public void startGame() {
		History.replaceItem("intro");
		History.fireCurrentHistoryState();
		events.fireEvent(new GameStartedEvent());
		startTurn();
	}
	
	public void endTurn() {
		events.fireEvent(new TurnEndedEvent());
	}
	
	public void startTurn() {
		events.fireEvent(new TurnStartedEvent());
	}

	public void message(String formattedMessage) {
		message(formattedMessage, "info");
	}

	public void message(String formattedMessage, String category) {
		events.fireEvent(new MessageEvent(formattedMessage, category));
	}
	
	@Override
	public void onModuleLoad() {
		page = HTMLPanel.wrap(DOM.getElementById("page"));
		events = GWT.create(SimpleEventBus.class);
		setupScreens();
		setupHistory();
		setupStarMap();
		startGame();
	}

}
