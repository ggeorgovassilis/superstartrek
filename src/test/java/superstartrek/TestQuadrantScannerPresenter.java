package superstartrek;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.shared.testing.CountingEventBus;

import superstartrek.client.Application;
import superstartrek.client.activities.computer.quadrantscanner.IQuadrantScannerView;
import superstartrek.client.activities.computer.quadrantscanner.QuadrantScannerPresenter;
import superstartrek.client.activities.computer.quadrantscanner.QuadrantScannerView;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.Klingon.ShipClass;
import superstartrek.client.activities.navigation.ThingMovedEvent;
import superstartrek.client.activities.navigation.ThingMovedHandler;
import superstartrek.client.activities.sector.contextmenu.SectorMenuPresenter;
import superstartrek.client.activities.sector.contextmenu.SectorMenuView;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Setup;
import superstartrek.client.model.Star;
import superstartrek.client.model.StarBase;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;

public class TestQuadrantScannerPresenter {

	QuadrantScannerPresenter presenter;
	Application app;
	CountingEventBus events;
	SectorMenuPresenter sectorMenuPresenter;
	StarMap map;
	IQuadrantScannerView view;
	
	@Before
	public void setup() {
		app = new Application();
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
		Location lFrom = new Location(4, 5);
		Location lTo = new Location(6, 7);
		enterprise.setQuadrant(qTo);

		map.setQuadrant(qFrom);
		map.setQuadrant(qTo);
		
		StarBase sb = new StarBase();
		sb.setLocation(new Location(1,7));
		qTo.setStarBase(sb);
		
		qTo.getStars().add(new Star(app, 6,5));
		qTo.getStars().add(new Star(app, 5,6));
		
		Klingon k = new Klingon(app, ShipClass.BirdOfPrey);
		k.setLocation(new Location(7,7));
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