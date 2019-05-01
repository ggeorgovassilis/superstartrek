package superstartrek;

import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.testing.CountingEventBus;

import superstartrek.client.Application;
import superstartrek.client.activities.sector.contextmenu.ISectorContextMenuView;
import superstartrek.client.activities.sector.contextmenu.SectorContextMenuPresenter;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;
import superstartrek.client.utils.BrowserAPI;

public class TestSectorContextMenuPresenter extends BaseTest{

	SectorContextMenuPresenter presenter;
	ISectorContextMenuView view;
	
	@Before
	public void setup() {
		presenter = new SectorContextMenuPresenter(application);
		view = mock(ISectorContextMenuView.class);
		presenter.setView(view);
	}
	
	@Test
	public void test() {
		Quadrant q = new Quadrant("test q", 3, 4);
		enterprise.setQuadrant(q);
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
		verify(view).enableButton("cmd_firePhasers", false);
		verify(view).enableButton("cmd_fireTorpedos", true);
	}
}
