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
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;

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
import superstartrek.client.activities.report.StatusReportPresenter;
import superstartrek.client.activities.report.StatusReportView;
import superstartrek.client.activities.messages.MessagesPresenter;
import superstartrek.client.activities.sector.scan.SectorScanPresenter;
import superstartrek.client.activities.sector.scan.ScanSectorView;
import superstartrek.client.control.GameController;
import superstartrek.client.control.GameOverEvent;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Setup;
import superstartrek.client.model.StarMap;
import superstartrek.client.utils.Browser;
import superstartrek.client.utils.GWTRandomNumberFactory;
import superstartrek.client.utils.GwtBrowserImpl;
import superstartrek.client.utils.Random;
import superstartrek.client.activities.messages.MessageHandler.MessagePostedEvent;
import superstartrek.client.activities.pwa.ApplicationUpdateCheckHandler;
import superstartrek.client.activities.pwa.UpdateAppPromptPresenter;
import superstartrek.client.activities.pwa.UpdateAppPromptView;;

public class Application
		implements EntryPoint, GamePhaseHandler, ApplicationUpdateCheckHandler{
	private static Logger log = Logger.getLogger("");

	public EventBus events;
	public HTMLPanel _page;
	public StarMap starMap;
	public Browser browser;
	Element logDiv;

	private static Application that;
	public GameController gameController;
	protected Resources resources;
	protected Set<String> flags;
	public Random random;
	
	public static Application get() {
		return that;
	}
	
	public Application() {
		if (Application.that!=null)
			GWT.log("There already is an application instance");
		Application.that = this;
	}
	
	public Quadrant getActiveQuadrant() {
		return starMap.enterprise.getQuadrant();
	}

	public Resources getResources() {
		return resources;
	}
	
	public void addHistoryListener(ValueChangeHandler<String> handler) {
		if (GWT.isClient()) // ignore in unit tests
			History.addValueChangeHandler(handler);
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
		new ScanSectorView(new SectorScanPresenter(this));
		new MessagesView(new MessagesPresenter(this));
		new LRSScreen(new LRSPresenter(this));
		new StatusReportView(new StatusReportPresenter(this));
		new UpdateAppPromptView(new UpdateAppPromptPresenter(this));
	}
	
	public void registerEventHandlers() {
		events.addHandler(GameOverEvent.TYPE, this);
		events.addHandler(ApplicationUpdateEvent.TYPE, this);
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
		Window.Location.reload();
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
	
	@Override
	public void onModuleLoad() {
		Application.that = this;
		random = new Random(new GWTRandomNumberFactory());
		resources = GWT.create(Resources.class);
		setUncaughtExceptionHandler();
		_page = HTMLPanel.wrap(RootPanel.getBodyElement());
		events = GWT.create(SimpleEventBus.class);
		//setupLogging();
		setupScreens();
		setupStarMap();
		setupGameController();
		starMap.enterprise.warpTo(starMap.enterprise.getQuadrant(), null);
		gameController.startGame();
		if (GWT.isClient())
			browser = new GwtBrowserImpl();
		//null out so that resources can be garbage collected
		resources = null;
	}
	
	@Override
	public void appMustReload() {
		reload();
	}


}
