package superstartrek;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import superstartrek.client.activities.sector.contextmenu.ISectorContextMenuView;
import superstartrek.client.activities.sector.contextmenu.SectorContextMenuPresenter;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;

public class TestSectorContextMenuPresenter extends BaseTest{

	SectorContextMenuPresenter presenter;
	ISectorContextMenuView view;
	
	@Before
	public void setup() {
		presenter = new SectorContextMenuPresenter();
		view = mock(ISectorContextMenuView.class);
		presenter.setView(view);
	}
	
	@Test
	public void test() {
		Quadrant q = new Quadrant("test q", 3, 4);
		enterprise.setQuadrant(q);
		//populate reachibility list
		enterprise.findReachableSectors();
		doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				ScheduledCommand cmd = invocation.getArgument(0, ScheduledCommand.class);
				cmd.execute();
				return null;
			}
		}).when(view).hide(any(ScheduledCommand.class));
		when(browser.getWindowWidthPx()).thenReturn(400);
		when(browser.getMetricWidthInPx()).thenReturn(10);
		when(browser.getMetricHeightInPx()).thenReturn(10);
		presenter.showMenu(66, 77, Location.location(1,2), q);
		verify(view).enableButton("cmd_navigate", true);
		verify(view).enableButton("cmd_firePhasers", false);
		verify(view).enableButton("cmd_fireTorpedos", true);
	}
}
