package superstartrek;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.shared.testing.CountingEventBus;

import superstartrek.client.Application;
import superstartrek.client.activities.sector.scan.IScanSectorView;
import superstartrek.client.activities.sector.scan.ScanSectorHandler;
import superstartrek.client.activities.sector.scan.SectorScanPresenter;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;

import static org.mockito.Mockito.*;

public class TestSectorScannerPresenter {

	SectorScanPresenter presenter;
	Application application;
	IScanSectorView view;
	CountingEventBus events;
	StarMap map;
	Enterprise enterprise;
	
	@Before
	public void setup() {
		application = new Application();
		application.events = events = new CountingEventBus();
		application.starMap = map = new StarMap();
		presenter = new SectorScanPresenter(application);
		view = mock(IScanSectorView.class);
		presenter.setView(view);
	}
	
	@After
	public void cleanup() {
		Application.set(null);
	}

	@Test
	public void testScanSector_notthing() {
		Quadrant q = new Quadrant("test quadrant", 1, 2);
		map.setQuadrant(q);
		Enterprise enterprise = new Enterprise(application, map);
		map.enterprise = enterprise;
		enterprise.setLocation(Location.location(0,0));
		enterprise.setQuadrant(q);
		Location l = Location.location(4,5);
		ScanSectorHandler.ScanSectorEvent event = new ScanSectorHandler.ScanSectorEvent(l, q);
		presenter.scanSector(event);
		
		verify(view).setObjectLocation(eq("4:5"));
		verify(view).setObjectName(eq("Nothing"));
		verify(view).setObjectQuadrant(eq("test quadrant"));
		verify(view).setProperty(eq("scan-report-shields"), eq("scan-report-shields-value"), eq("hidden"), eq(""));
		verify(view).setProperty(eq("scan-report-weapons"), eq("scan-report-weapons-value"), eq("hidden"), eq(""));
		verify(view).setProperty(eq("scan-report-engines"), eq("scan-report-engines-value"), eq("hidden"), eq(""));

	}

	@Test
	public void testScanSector_enterprise() {
		Quadrant q = new Quadrant("test quadrant", 1, 2);
		map.setQuadrant(q);
		Enterprise enterprise = new Enterprise(application, map);
		map.enterprise = enterprise;
		enterprise.setLocation(Location.location(0,0));
		enterprise.setQuadrant(q);
		Location l = Location.location(0,0);
		ScanSectorHandler.ScanSectorEvent event = new ScanSectorHandler.ScanSectorEvent(l, q);
		presenter.scanSector(event);
		
		verify(view).setObjectLocation(eq("0:0"));
		verify(view).setObjectName(eq("NCC 1701 USS Enterprise"));
		verify(view).setObjectQuadrant(eq("test quadrant"));
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

