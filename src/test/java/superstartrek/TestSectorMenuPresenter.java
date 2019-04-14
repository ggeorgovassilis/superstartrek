package superstartrek;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.testing.CountingEventBus;

import superstartrek.client.Application;
import superstartrek.client.activities.sector.contextmenu.ISectorMenuView;
import superstartrek.client.activities.sector.contextmenu.SectorContextMenuPresenter;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;
import superstartrek.client.utils.Browser;

public class TestSectorMenuPresenter {

	SectorContextMenuPresenter presenter;
	Application app;
	CountingEventBus events;
	StarMap map;
	ISectorMenuView view;
	Browser browser;
	
	@Before
	public void setup() {
		app = Application.get();
		app.events = events = new CountingEventBus();
		app.starMap = new StarMap();
		app.browser = browser = mock(Browser.class);
		presenter = new SectorContextMenuPresenter(app);
		view = mock(ISectorMenuView.class);
		presenter.setView(view);
	}

	@Test
	public void test() {
		app.starMap = map = new StarMap();
		Quadrant q = new Quadrant("test q", 3, 4);
		Enterprise e = new Enterprise(app);
		e.setQuadrant(q);
		map.enterprise = e;
		doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				ScheduledCommand cmd = invocation.getArgumentAt(0, ScheduledCommand.class);
				cmd.execute();
				return null;
			}
		}).when(view).hide(any(ScheduledCommand.class));
		when(browser.getWindowWidthPx()).thenReturn(400);
		when(view.getMetricWidthInPx()).thenReturn(10);
		when(view.getMetricHeightInPx()).thenReturn(10);
		presenter.showMenu(66, 77, Location.location(1,2), q);
		verify(view).enableButton("cmd_navigate", true);
		verify(view).enableButton("cmd_firePhasers", true);
		verify(view).enableButton("cmd_fireTorpedos", true);
		verify(view).enableButton("cmd_toggleFireAtWill", true);
	}
}
