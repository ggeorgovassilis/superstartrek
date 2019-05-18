package superstartrek;

import org.junit.Before;
import org.junit.Test;

import superstartrek.client.activities.pwa.Callback;
import superstartrek.client.activities.sector.scan.IScanSectorView;
import superstartrek.client.activities.sector.scan.ScanSectorHandler;
import superstartrek.client.activities.sector.scan.ScanSectorPresenter;
import superstartrek.client.bus.Events;
import superstartrek.client.model.Location;
import static org.mockito.Mockito.*;

public class TestSectorScannerPresenter extends BaseTest{

	ScanSectorPresenter presenter;
	IScanSectorView view;
	
	@Before
	public void setup() {
		presenter = new ScanSectorPresenter(application);
		view = mock(IScanSectorView.class);
		presenter.setView(view);
	}
	
	@Test
	public void testScanSector_notthing() {
		enterprise.setLocation(Location.location(0,0));
		Location l = Location.location(4,5);
		bus.fireEvent(Events.SCAN_SECTOR, (Callback<ScanSectorHandler>)(h)->h.scanSector(l, quadrant));
		
		verify(view).setObjectLocation(eq("4:5"));
		verify(view).setObjectName(eq("Nothing"));
		verify(view).setObjectQuadrant(eq("test quadrant 1:2"));
		verify(view).setProperty(eq("scan-report-shields"), eq("scan-report-shields-value"), eq("hidden"), eq(""));
		verify(view).setProperty(eq("scan-report-weapons"), eq("scan-report-weapons-value"), eq("hidden"), eq(""));
		verify(view).setProperty(eq("scan-report-engines"), eq("scan-report-engines-value"), eq("hidden"), eq(""));

	}

	@Test
	public void testScanSector_enterprise() {
		enterprise.setLocation(Location.location(0,0));
		Location l = Location.location(0,0);
		bus.fireEvent(Events.SCAN_SECTOR, (Callback<ScanSectorHandler>)(h)->h.scanSector(l, quadrant));
		
		verify(view).setObjectLocation(eq("0:0"));
		verify(view).setObjectName(eq("NCC 1701 USS Enterprise"));
		verify(view).setObjectQuadrant(eq("test quadrant 1:2"));
		verify(view).setProperty(eq("scan-report-shields"), eq("scan-report-shields-value"), eq(""), eq("%100"));
		verify(view).setProperty(eq("scan-report-weapons"), eq("scan-report-weapons-value"), eq(""), eq("online"));
		verify(view).setProperty(eq("scan-report-engines"), eq("scan-report-engines-value"), eq(""), eq("online"));
	}
	
	@Test
	public void test_doneWithMenu() {
		when(view.isVisible()).thenReturn(true);
		presenter.doneWithMenu();
		verify(view).hide();
	}
}

