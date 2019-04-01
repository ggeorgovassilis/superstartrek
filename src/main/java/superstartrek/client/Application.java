package superstartrek.client;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;

import superstartrek.client.activities.computer.ComputerPresenter;
import superstartrek.client.activities.computer.ComputerView;
import superstartrek.client.activities.intro.IntroPresenter;
import superstartrek.client.activities.intro.IntroView;
import superstartrek.client.activities.loading.LoadingPresenter;
import superstartrek.client.activities.loading.LoadingScreen;
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
import superstartrek.client.control.GameController;
import superstartrek.client.control.GameOverEvent;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.GameStartedEvent;
import superstartrek.client.control.KlingonTurnEvent;
import superstartrek.client.control.TurnEndedEvent;
import superstartrek.client.control.TurnStartedEvent;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Setup;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;
import superstartrek.client.pwa.ApplicationUpdateCheckHandler;
import superstartrek.client.pwa.ApplicationUpdateEvent;
import superstartrek.client.pwa.PWA;

public class Application
		implements EntryPoint, GamePhaseHandler, MessageHandler, ApplicationUpdateCheckHandler {

	public EventBus events;
	public HTMLPanel page;
	public StarMap starMap;
	public boolean gameIsRunning = true;
	protected boolean endTurnPending = false;
	protected boolean startTurnPending = false;

	private static Application that;
	public GameController gameController;
	protected Resources resources;
	protected Set<String> flags;
	
	public static Application get() {
		if (that == null)
			that = new Application();
		return that;
	}
	
	public Application() {
		if (Application.that!=null)
			throw new RuntimeException("There already is an application instance");
		Application.that = this;
	}

	public Resources getResources() {
		return resources;
	}
	
	public void addHistoryListener(ValueChangeHandler<String> handler) {
		if (GWT.isClient()) // ignore in unit tests
			History.addValueChangeHandler(handler);
	}
	
	public void endTurnAfterThis() {
		if (endTurnPending)
			return;
		endTurnPending = true;
		superstartrek.client.utils.Timer.postpone(new Scheduler.ScheduledCommand() {
			
			@Override
			public void execute() {
				endTurnPending = false;
				endTurn();
				startTurnAfterThis();
			}
		});
	}

	protected void setupStarMap() {
		Setup setup = new Setup(this);
		starMap = setup.createNewMap();
	}

	protected void setupScreens() {
		//since every presenter registers itself as an event listener they won't be garbage-collected, so we don't need to keep references to them
		new LoadingScreen(new LoadingPresenter(this));
		new IntroView(new IntroPresenter(this));
		new ManualScreen(new ManualPresenter(this));
		new ComputerView(new ComputerPresenter(this));
		new ScanSectorView(new ScanSectorPresenter(this));
		new MessagesView(new MessagesPresenter(this));
		new LRSScreen(new LRSPresenter(this));
		new StatusReportView(new StatusReportPresenter(this));
	}
	
	public void registerEventHandlers() {
		events.addHandler(GameOverEvent.TYPE, this);
		events.addHandler(MessageEvent.TYPE, this);
		events.addHandler(ApplicationUpdateEvent.TYPE, this);
	}

	public void startGame() {
		History.replaceItem("intro");
		History.fireCurrentHistoryState();
		registerEventHandlers();
		events.fireEvent(new GameStartedEvent());
	}

	public void endTurn() {
		events.fireEvent(new TurnEndedEvent());
		events.fireEvent(new KlingonTurnEvent());
		//release resources so that it can be (hopefully) garbage collected; at this point, everyone who needs resources should have them
		resources = null;
	}
	
	public void startTurnAfterThis() {
		if (startTurnPending)
			return;
		startTurnPending = true;
		superstartrek.client.utils.Timer.postpone(new Scheduler.ScheduledCommand() {
			
			@Override
			public void execute() {
				startTurnPending = false;
				startTurn();
			}
		});
	}

	public void startTurn() {
		GWT.log("------------------------------ new turn");
		starMap.advanceStarDate(1);
		events.fireEvent(new TurnStartedEvent());
	}

	public void message(String formattedMessage) {
		message(formattedMessage, "info");
	}

	public void message(String formattedMessage, String category) {
		events.fireEvent(new MessageEvent(MessageEvent.Action.show, formattedMessage, category));
	}

	/**
	 * * Set an uncaught exception handler that unwraps the exception *
	 * (UmbrellaException) for SuperDevMode.
	 */
	private void setUncaughtExceptionHandler() {
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			@Override
			public void onUncaughtException(Throwable e) {
				Throwable unwrapped = unwrap(e);
				GWT.log(e.getMessage(), unwrapped);
//				Logger.getAnonymousLogger().log(Level.SEVERE, "onUncaughtException " + e.getMessage(), unwrapped);
			}

			public Throwable unwrap(Throwable e) {
				if (e instanceof UmbrellaException) {
					UmbrellaException ue = (UmbrellaException) e;
					if (ue.getCauses().size() == 1) {
						return unwrap(ue.getCauses().iterator().next());
					}
				}
				return e;
			}
		});

	}

	public void gameOver(GameOverEvent.Outcome outcome, String reason) {
		GWT.log(reason);
		events.fireEvent(new GameOverEvent(outcome, reason));
	}

	@Override
	public void gameOver() {
		message("Game over.");
		this.gameIsRunning = false;
	}

	@Override
	public void gameLost() {
		message("The Enterprise was destroyed.", "gameover");
	}

	@Override
	public void gameWon() {
		message("Congratulations, all Klingons were destroyed.", "gamewon");
	}

	
	public Set<String> getFlags(){
		if (flags==null) {
			String sflags = Window.Location.getParameter("flags");
			if (sflags==null)
				sflags = "";
			String[] split = sflags.split(",");
			flags = new HashSet<>();
			for (String s:split)
				flags.add(s);
		}
		return flags;
	}

	@Override
	public void messagesAcknowledged() {
		//TODO: this is too implicit. The intention is that, once the game has been lost/won and the user clicks away the informing message, the game should reload.
		if (!this.gameIsRunning)
			Window.Location.reload();
	}
	
	@Override
	public void newVersionAvailable() {
		message("A new version is available","product-info");
		message("If you want to update, please uninstall this version first","product-info");
	}

	@Override
	public void versionIsCurrent() {
	}

	@Override
	public void checkFailed() {
	}
	
	public void setupGameController() {
		gameController = new GameController(this);
	}

	@Override
	public void onModuleLoad() {
		GWT.log("onModuleLoad");
		resources = GWT.create(Resources.class);
		setUncaughtExceptionHandler();
		page = HTMLPanel.wrap(DOM.getElementById("page"));
		events = GWT.create(SimpleEventBus.class);
		setupScreens();
		setupStarMap();
		setupGameController();
		startGame();
		starMap.enterprise.warpTo(starMap.enterprise.getQuadrant(), null);
		startTurnAfterThis();
		new PWA(this).run();
	}


}
