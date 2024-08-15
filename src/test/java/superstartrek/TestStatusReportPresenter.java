package superstartrek;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import superstartrek.client.activities.report.StatusReportPresenter;
import superstartrek.client.activities.report.StatusReportScreen;
import superstartrek.client.control.ScoreKeeper;

public class TestStatusReportPresenter extends BaseTest{

	StatusReportPresenter presenter;
	StatusReportScreen view;
	
	@Before
	public void init() {
		presenter = new StatusReportPresenter();
		view = mock(StatusReportScreen.class);
		application.scoreKeeper = mock(ScoreKeeper.class);
		presenter.setView(view);
	}
	
	@Test
	public void testStatusReportUpdate() {
		presenter.updateView();
		verify(view).setProperty("report_stardate", "2100", false);
		verify(view).setProperty("report_score", "0", false);
		verify(view).setProperty("report_location", "test quadrant 1:2", false);
		verify(view).setProperty("report_max_impulse", "%100.0", false);
		//TODO cover all properties
	}

	@Test
	public void testStatusReportUpdate_damaged_impulse() {
		enterprise.getImpulse().damage(1, starMap.getStarDate());
		presenter.updateView();
		verify(view).setProperty("report_stardate", "2100", false);
		verify(view).setProperty("report_max_impulse", "%66.0", true);
		//TODO cover all properties
	}
}
