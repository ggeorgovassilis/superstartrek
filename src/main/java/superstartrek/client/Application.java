package superstartrek.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
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
import superstartrek.client.activities.klingons.KlingonTurnEvent;
import superstartrek.client.activities.loading.GameOverEvent;
import superstartrek.client.activities.loading.GameOverHandler;
import superstartrek.client.activities.loading.GameStartedEvent;
import superstartrek.client.activities.loading.LoadingPresenter;
import superstartrek.client.activities.loading.LoadingScreen;
import superstartrek.client.activities.loading.GameOverEvent.Outcome;
import superstartrek.client.activities.lrs.LRSEvent;
import superstartrek.client.activities.lrs.LRSEvent.Action;
import superstartrek.client.activities.lrs.LRSPresenter;
import superstartrek.client.activities.lrs.LRSScreen;
import superstartrek.client.activities.manual.ManualPresenter;
import superstartrek.client.activities.manual.ManualScreen;
import superstartrek.client.activities.messages.MessagesView;
import superstartrek.client.activities.navigation.EnterpriseWarpedEvent;
import superstartrek.client.activities.navigation.EnterpriseWarpedHandler;
import superstartrek.client.activities.navigation.ThingMovedEvent;
import superstartrek.client.activities.navigation.ThingMovedHandler;
import superstartrek.client.activities.report.StatusReportPresenter;
import superstartrek.client.activities.report.StatusReportView;
import superstartrek.client.activities.messages.MessageEvent;
import superstartrek.client.activities.messages.MessageHandler;
import superstartrek.client.activities.messages.MessagesPresenter;
import superstartrek.client.activities.sector.scan.ScanSectorPresenter;
import superstartrek.client.activities.sector.scan.ScanSectorView;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Setup;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;

public class Application implements EntryPoint, EnterpriseWarpedHandler, ThingMovedHandler, GameOverHandler, MessageHandler{

	public EventBus events;
	public HTMLPanel page;
	public StarMap starMap;
	public boolean gameIsRunning=true;
	protected boolean endTurnPending = false;

	public LoadingPresenter loadingPresenter;
	public ComputerPresenter computerPresenter;
	public IntroPresenter introPresenter;
	public GlassPanelPresenter glassPanelPresenter;
	public ManualPresenter manualPresenter;
	public ScanSectorPresenter scanSectorPresenter;
	public MessagesPresenter messagesPresenter;
	public LRSPresenter lrsPresenter;
	public StatusReportPresenter statusReportPresenter;
	
	public void endTurnAfterThis() {
		if (endTurnPending)
			return;
		endTurnPending = true;
		superstartrek.client.utils.Timer.postpone(new Runnable() {
			
			@Override
			public void run() {
				endTurnPending = false;
				endTurn();
				startTurn();
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
		
		lrsPresenter = new LRSPresenter(this);
		new LRSScreen(lrsPresenter);
		
		statusReportPresenter = new StatusReportPresenter(this);
		new StatusReportView(statusReportPresenter);
		
	}
	
	public void startGame() {
		History.replaceItem("intro");
		History.fireCurrentHistoryState();
		events.addHandler(EnterpriseWarpedEvent.TYPE, this);
		events.addHandler(ThingMovedEvent.TYPE, this);
		events.addHandler(GameOverEvent.TYPE, this);
		events.addHandler(MessageEvent.TYPE, this);
		events.fireEvent(new GameStartedEvent());
	}
	
	public void endTurn() {
		events.fireEvent(new TurnEndedEvent());
		events.fireEvent(new KlingonTurnEvent());
	}
	
	public void startTurn() {
		starMap.advanceStarDate(1);
		events.fireEvent(new TurnStartedEvent());
	}

	public void message(String formattedMessage) {
		message(formattedMessage, "info");
	}

	public void message(String formattedMessage, String category) {
		events.fireEvent(new MessageEvent(MessageEvent.Action.show, formattedMessage, category));
	}
	
	@Override
	public void onModuleLoad() {
		GWT.log("onModuleLoad");
		page = HTMLPanel.wrap(DOM.getElementById("page"));
		events = GWT.create(SimpleEventBus.class);
		setupScreens();
		setupStarMap();
		startGame();
		starMap.enterprise.warpTo(starMap.enterprise.getQuadrant());
	}

	@Override
	public void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
		if (thing == starMap.enterprise)
			endTurnAfterThis();
	}

	@Override
	public void onEnterpriseWarped(Enterprise enterprise, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
		endTurnAfterThis();
	}

	public void gameOver(GameOverEvent.Outcome outcome) {
		events.fireEvent(new GameOverEvent(outcome));
	}

	@Override
	public void gameOver() {
		message("Game over.");
		this.gameIsRunning = false;
	}

	@Override
	public void gameLost() {
		message("The Enterprise was destroyed.");
	}

	@Override
	public void gameWon() {
		message("Congratulations, all Klingons were destroyed.");
	}

	@Override
	public void showMessage(String formattedMessage, String category) {
	}

	@Override
	public void messagesHidden() {
		if (!this.gameIsRunning)
			Window.Location.reload();
	}

}
