package superstartrek.client;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;

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
import superstartrek.client.activities.messages.MessagesPresenter;
import superstartrek.client.activities.sector.scan.ScanSectorPresenter;
import superstartrek.client.activities.sector.scan.ScanSectorView;
import superstartrek.client.control.GameController;
import superstartrek.client.control.GameOverEvent;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Setup;
import superstartrek.client.model.StarMap;
import superstartrek.client.utils.BrowserAPI;
import superstartrek.client.utils.GwtBrowserAPIImpl;
import superstartrek.client.activities.messages.MessageHandler.MessagePostedEvent;
import superstartrek.client.activities.pwa.AppInstallPromptPresenter;
import superstartrek.client.activities.pwa.AppInstallPromptView;
import superstartrek.client.activities.pwa.ApplicationLifecycleHandler;
import superstartrek.client.activities.pwa.PWA;
import superstartrek.client.activities.pwa.UpdateAppPromptPresenter;
import superstartrek.client.activities.pwa.UpdateAppPromptView;
import superstartrek.client.activities.pwa.http.RequestFactory;
import superstartrek.client.activities.pwa.http.RequestFactoryBrowserImpl;;

public class Application
		implements EntryPoint, GamePhaseHandler, ApplicationLifecycleHandler{
	private static Logger log = Logger.getLogger("");

	public EventBus events;
	public HTMLPanel _page;
	public StarMap starMap;
	public BrowserAPI browserAPI;
	public RequestFactory requestFactory;
	public PWA pwa;
	Element logDiv;

	private static Application that;
	public GameController gameController;
	protected Resources resources;
	protected Set<String> flags;
	
	public static void set(Application app) {
		that = app;
	}
	
	public static Application get() {
		return that;
	}
	
	public Application() {
		if (get()!=null)
			throw new RuntimeException("Application already instantiated");
		set(this);
	}
	
	public Quadrant getActiveQuadrant() {
		return starMap.enterprise.getQuadrant();
	}

	public Resources getResources() {
		return resources;
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
		new ComputerScreen(new ComputerPresenter(this));
		new ScanSectorView(new ScanSectorPresenter(this));
		new MessagesView(new MessagesPresenter(this));
		new LRSScreen(new LRSPresenter(this));
		new StatusReportView(new StatusReportPresenter(this));
		new UpdateAppPromptView(new UpdateAppPromptPresenter(this));
		new AppMenuView(new AppMenuPresenter(this));
		new AppInstallPromptView(new AppInstallPromptPresenter(this));
	}
	
	public void registerEventHandlers() {
		events.addHandler(GameOverEvent.TYPE, this);
		events.addHandler(ApplicationLifecycleEvent.TYPE, this);
	}


	public void message(String formattedMessage) {
		message(formattedMessage, "info");
	}

	public void message(String formattedMessage, String category) {
		events.fireEvent(new MessagePostedEvent(formattedMessage, category));
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

	public void reload() {
		browserAPI.reloadApplication();
	}
	
	public void setupGameController() {
		gameController = new GameController(this);
	}

	void setupLogging() {
		if (GWT.isClient()) {
			logDiv = DOM.createDiv();
			RootPanel.get().getElement().appendChild(logDiv);
			log.addHandler(new Handler() {
				
				@Override
				public void publish(LogRecord record) {
					Element entry = DOM.createDiv();
					entry.setInnerText(record.getMessage());
					logDiv.appendChild(entry);
				}
				
				@Override
				public void flush() {
				}
				
				@Override
				public void close() throws SecurityException {
				}
			});
		}
	}
	
	public void setupHttp() {
		if (GWT.isClient()) {
			requestFactory = new RequestFactoryBrowserImpl();
		}
	}
	
	public void setupPwa() {
		pwa = new PWA(this);
		pwa.setRequestFactory(requestFactory);
		pwa.run((v)->{
			log.info("PWA initialised, game can start");
			starMap.enterprise.warpTo(starMap.enterprise.getQuadrant(), null);
			gameController.startGame();
			//null out so that resources can be garbage collected
			resources = null;
		});
	}
	
	@Override
	public void onModuleLoad() {
		Application.that = this;
		setUncaughtExceptionHandler();
		resources = GWT.create(Resources.class);
		if (GWT.isClient())
			browserAPI = new GwtBrowserAPIImpl();
		_page = HTMLPanel.wrap(RootPanel.getBodyElement());
		events = GWT.create(SimpleEventBus.class);
		//setupLogging();
		setupScreens();
		setupStarMap();
		setupGameController();
		setupHttp();
		setupPwa();
	}
	
	@Override
	public void appMustReload() {
		reload();
	}
	
	@Override
	public void filesAreCached() {
		GWT.log("Files are now cached for offline use");
	}


}
