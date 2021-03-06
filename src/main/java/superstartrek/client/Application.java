package superstartrek.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.UmbrellaException;

import superstartrek.client.activities.appmenu.AppMenuPresenter;
import superstartrek.client.activities.appmenu.AppMenuView;
import superstartrek.client.activities.computer.ComputerPresenter;
import superstartrek.client.activities.computer.ComputerScreen;
import superstartrek.client.activities.intro.IntroPresenter;
import superstartrek.client.activities.intro.IntroView;
import superstartrek.client.activities.loading.LoadingPresenter;
import superstartrek.client.activities.loading.LoadingScreen;
import superstartrek.client.activities.lrs.LRSPresenter;
import superstartrek.client.activities.lrs.LRSScreen;
import superstartrek.client.activities.manual.ManualPresenter;
import superstartrek.client.activities.manual.ManualScreen;
import superstartrek.client.activities.messages.MessagesView;
import superstartrek.client.activities.report.StatusReportPresenter;
import superstartrek.client.activities.report.StatusReportView;
import superstartrek.client.activities.settings.SettingsPresenter;
import superstartrek.client.activities.settings.SettingsScreen;
import superstartrek.client.activities.messages.MessagesPresenter;
import superstartrek.client.bus.Commands;
import superstartrek.client.bus.EventBus;
import superstartrek.client.bus.Events;
import superstartrek.client.control.GameController;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.ScoreKeeper;
import superstartrek.client.control.ScoreKeeperImpl;
import superstartrek.client.credits.CreditsPresenter;
import superstartrek.client.credits.CreditsScreen;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Setup;
import superstartrek.client.model.StarMap;
import superstartrek.client.persistence.GameSaver;
import superstartrek.client.screentemplates.ScreenTemplates;
import superstartrek.client.utils.BrowserAPI;
import superstartrek.client.utils.GwtBrowserAPIImpl;
import superstartrek.client.utils.Strings;
import superstartrek.client.utils.Timer;
import superstartrek.client.activities.pwa.AppInstallPromptPresenter;
import superstartrek.client.activities.pwa.AppInstallPromptView;
import superstartrek.client.activities.pwa.ApplicationLifecycleHandler;
import superstartrek.client.activities.pwa.Callback;
import superstartrek.client.activities.pwa.PWA;
import superstartrek.client.activities.pwa.UpdateAppPromptPresenter;
import superstartrek.client.activities.pwa.UpdateAppPromptView;
import superstartrek.client.activities.pwa.http.RequestFactory;
import superstartrek.client.activities.pwa.http.RequestFactoryBrowserImpl;

public class Application implements EntryPoint, GamePhaseHandler, ApplicationLifecycleHandler {

	public final static String UI_SCALE_KEY = "UI_SCALE";
	public final static String UI_THEME_KEY = "UI_THEME";
	private static Logger log = Logger.getLogger("");

	public EventBus eventBus = new EventBus();
	public StarMap starMap;
	public BrowserAPI browserAPI;
	public RequestFactory requestFactory;
	public PWA pwa;
	public GameSaver gameSaver;
	public ScoreKeeper scoreKeeper = new ScoreKeeperImpl();

	private static Application that;
	public GameController gameController;
	protected ScreenTemplates screenTemplates;

	public static void set(Application app) {
		that = app;
	}

	public static Application get() {
		return that;
	}

	public Application() {
		if (get() != null)
			throw new RuntimeException("Application already instantiated");
		set(this);
	}

	public Quadrant getActiveQuadrant() {
		return starMap.enterprise.getQuadrant();
	}

	public ScreenTemplates getScreenTemplates() {
		return screenTemplates;
	}

	protected void setupStarMap() {
		Setup setup = new Setup(this);
		starMap = setup.createNewMap();
	}

	protected void setupScreens() {
		// since every presenter registers itself as an event listener they won't be
		// garbage-collected, so we don't need to keep references to them
		setUIScale(getUIScale());
		setUITheme(getUITheme());
		new LoadingScreen(new LoadingPresenter(this));
		new IntroView(new IntroPresenter(this));
		new ManualScreen(new ManualPresenter(this));
		new ComputerScreen(new ComputerPresenter(this, scoreKeeper));
		new MessagesView(new MessagesPresenter(this));
		new LRSScreen(new LRSPresenter(this));
		new StatusReportView(new StatusReportPresenter(this));
		new UpdateAppPromptView(new UpdateAppPromptPresenter(this));
		new AppMenuView(new AppMenuPresenter(this));
		new AppInstallPromptView(new AppInstallPromptPresenter(this));
		new SettingsScreen(new SettingsPresenter(this));
		new CreditsScreen(new CreditsPresenter(this));
	}

	public void message(String formattedMessage) {
		message(formattedMessage, "info");
	}

	public void message(String formattedMessage, String category) {
		eventBus.fireEvent(Events.MESSAGE_POSTED, (h) -> h.messagePosted(formattedMessage, category));
	}

	/**
	 * * Set an uncaught exception handler that unwraps the exception *
	 * (UmbrellaException) for SuperDevMode.
	 */
	private void setUncaughtExceptionHandler() {
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			@Override
			public void onUncaughtException(Throwable e) {
				log.log(Level.SEVERE, e.getMessage(), e);
				Throwable unwrapped = unwrap(e);
				log.log(Level.SEVERE, e.getMessage(), unwrapped);
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

	public void reload() {
		browserAPI.reloadApplication();
	}

	public void restart() {
		eventBus.fireEvent(Events.GAME_RESTART, (h) -> h.beforeGameRestart());
		gameSaver.deleteGame();
		startGame();
	}

	public void setupGameController() {
		gameController = new GameController(this, scoreKeeper);
	}

	public void setupHttp() {
		if (GWT.isClient()) {
			requestFactory = new RequestFactoryBrowserImpl();
		}
	}

	public void setupTheRest() {
		setupScreens();
		setupGameController();
		startGame();
	}

	public void startGame() {
		setupStarMap();
		Timer.postpone(() -> {
			starMap.enterprise.warpTo(starMap.enterprise.getQuadrant(), null);
			gameController.startGame();
			// null out so that resources can be garbage collected; by now everyone who
			// needs them during initialisation has gotten them already
			screenTemplates = null;
		});
	}

	public void setupPwa(Callback<Void> callback) {
		pwa = new PWA(this);
		pwa.setRequestFactory(requestFactory);
		pwa.run(callback);
	}

	public void registerEventHandlers() {
		eventBus.addHandler(Events.GAME_OVER, this);
		eventBus.addHandler(Commands.RELOAD_APP, this);
		browserAPI.addWindowResizeHandler((e)->{
			eventBus.fireEvent(Events.SCREEN_RESIZES, h->h.onScreenResize());
			eventBus.fireEvent(Events.SCREEN_RESIZES, h->h.onAfterScreenResize(e.getWidth(), e.getHeight()));
		});
	}

	public void setupGameSaver() {
		gameSaver = new GameSaver(this);
	}

	@Override
	public void onModuleLoad() {
		Application.that = this;
		setUncaughtExceptionHandler();
		if (GWT.isClient())
			browserAPI = new GwtBrowserAPIImpl(eventBus);
		screenTemplates = GWT.create(ScreenTemplates.class);
		setupHttp();
		registerEventHandlers();
		setupGameSaver();
		setupPwa((v) -> setupTheRest());
	}

	@Override
	public void appMustReload() {
		reload();
	}

	public void setUIScale(String scale) {
		browserAPI.removeGlobalCss("ui-scale-small");
		browserAPI.removeGlobalCss("ui-scale-medium");
		browserAPI.removeGlobalCss("ui-scale-large");
		browserAPI.removeGlobalCss("ui-scale-xl");
		browserAPI.addGlobalCss("ui-scale-" + scale);
		browserAPI.storeValueLocally(UI_SCALE_KEY, scale);
	}
	
	public void setUITheme(String theme) {
		browserAPI.storeValueLocally(UI_THEME_KEY, theme);
		browserAPI.removeGlobalCss("ui-theme-highcontrast");
		browserAPI.removeGlobalCss("ui-theme-default");
		browserAPI.addGlobalCss("ui-theme-"+theme);
	}
	
	public String getUITheme() {
		String theme = browserAPI.getLocallyStoredValue(UI_THEME_KEY);
		if (Strings.isEmpty(theme))
			theme = "default";
		return theme;
	}

	public String getUIScale() {
		String scale = browserAPI.getLocallyStoredValue(UI_SCALE_KEY);
		if (Strings.isEmpty(scale))
			scale="medium";
		return scale;
	}

}
