package superstartrek;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import superstartrek.client.activities.computer.sectorcontextmenu.SectorContextMenuPresenter;
import superstartrek.client.activities.computer.sectorcontextmenu.SectorContextMenuView;
import superstartrek.client.space.Location;
import superstartrek.client.space.Quadrant;

public class TestSectorContextMenuPresenter extends BaseTest{

	SectorContextMenuPresenter presenter;
	SectorContextMenuView view;
	
	@Before
	public void setup() {
		presenter = new SectorContextMenuPresenter();
		view = mock(SectorContextMenuView.class);
		presenter.setView(view);
	}
	
	@Test
	public void test() {
		Quadrant q = new Quadrant("test q", 3, 4);
		enterprise.setQuadrant(q);
		//populate reachibility list
		enterprise.updateReachableSectors();
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
