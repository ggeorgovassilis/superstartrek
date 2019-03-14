package superstartrek;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.shared.testing.CountingEventBus;

import superstartrek.client.Application;
import superstartrek.client.activities.computer.quadrantscanner.IQuadrantScannerView;
import superstartrek.client.activities.computer.quadrantscanner.QuadrantScannerPresenter;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.Klingon.ShipClass;
import superstartrek.client.activities.sector.contextmenu.SectorMenuPresenter;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Star;
import superstartrek.client.model.StarBase;
import superstartrek.client.model.StarMap;

public class TestQuadrantScannerPresenter {

	QuadrantScannerPresenter presenter;
	Application app;
	CountingEventBus events;
	SectorMenuPresenter sectorMenuPresenter;
	StarMap map;
	IQuadrantScannerView view;
	
	@Before
	public void setup() {
		app = Application.get();
		app.events = new CountingEventBus();
		sectorMenuPresenter = mock(SectorMenuPresenter.class);
		presenter = new QuadrantScannerPresenter(app, sectorMenuPresenter);
		app.starMap = map = new StarMap();
		
		view = mock(IQuadrantScannerView.class);
		presenter.setView(view);
		
	}

	@Test
	public void testOnEnterpriseWarped() {
		Enterprise enterprise = map.enterprise = new Enterprise(app);
		Quadrant qFrom = new Quadrant("from 1 2", 1, 2);
		Quadrant qTo = new Quadrant("to 3 4", 3, 4);
		Location lFrom = Location.location(4, 5);
		Location lTo = Location.location(6, 7);
		enterprise.setQuadrant(qTo);

		map.setQuadrant(qFrom);
		map.setQuadrant(qTo);
		
		StarBase sb = new StarBase();
		sb.setLocation(Location.location(1,7));
		qTo.setStarBase(sb);
		
		qTo.getStars().add(new Star(6,5));
		qTo.getStars().add(new Star(5,6));
		
		Klingon k = new Klingon(ShipClass.BirdOfPrey);
		k.setLocation(Location.location(7,7));
		qTo.getKlingons().add(k);
		
		presenter.onEnterpriseWarped(enterprise, qFrom, lFrom, qTo, lTo);
		verify(view).updateSector(eq(0), eq(0), eq("O=Ξ"), eq("enterprise "));
		verify(view, times(59)).updateSector(any(int.class), any(int.class), eq(""), eq(""));
		verify(view).updateSector(5, 6, "*", "star");
		verify(view).updateSector(6, 5, "*", "star");
		verify(view).updateSector(1, 7, "&lt;!&gt;", "starbase");
		verify(view).updateSector(7, 7, "C-D", "klingon cloaked ");
		
	}
}
