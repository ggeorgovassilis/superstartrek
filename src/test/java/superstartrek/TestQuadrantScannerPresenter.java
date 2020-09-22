package superstartrek;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.dom.client.KeyCodes;

import superstartrek.client.Application;
import superstartrek.client.activities.computer.quadrantscanner.IQuadrantScannerView;
import superstartrek.client.activities.computer.quadrantscanner.QuadrantScannerPresenter;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.Klingon.ShipClass;
import superstartrek.client.activities.sector.contextmenu.SectorContextMenuPresenter;
import superstartrek.client.activities.sector.contextmenu.SectorSelectedHandler;
import superstartrek.client.bus.Events;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Star;
import superstartrek.client.model.StarBase;
import superstartrek.client.model.Star.StarClass;

public class TestQuadrantScannerPresenter extends BaseTest{

	QuadrantScannerPresenter presenter;
	SectorContextMenuPresenter sectorMenuPresenter;
	IQuadrantScannerView view;
	
	@Before
	public void setup() {
		sectorMenuPresenter = mock(SectorContextMenuPresenter.class);
		presenter = new QuadrantScannerPresenter(application, sectorMenuPresenter);
		view = mock(IQuadrantScannerView.class);
		presenter.setView(view);
	}

	@After
	public void cleanup() {
		Application.set(null);
	}

	@Test
	public void test_onActiveQuadrantChanged() {
		Quadrant qFrom = new Quadrant("from 1 2", 1, 2);
		Quadrant qTo = new Quadrant("to 3 4", 3, 4);
		enterprise.setQuadrant(qTo);

		starMap.setQuadrant(qFrom);
		starMap.setQuadrant(qTo);
		
		StarBase sb = new StarBase();
		sb.setLocation(Location.location(1,7));
		qTo.setStarBase(sb);
		
		qTo.add(new Star(6,5, StarClass.A));
		qTo.add(new Star(5,6, StarClass.A));
		
		Klingon k = new Klingon(ShipClass.BirdOfPrey);
		k.setLocation(Location.location(7,7));
		qTo.add(k);
		
		presenter.onActiveQuadrantChanged(qFrom, qTo);
		verify(view).updateSector(eq(0), eq(0), eq("O=Ξ"), eq("enterprise "));
		verify(view, times(60)).clearSector(any(int.class), any(int.class));
		verify(view).updateSector(5, 6, StarClass.A.symbol, "star star-class-a");
		verify(view).updateSector(6, 5, StarClass.A.symbol, "star star-class-a");
		verify(view).updateSector(1, 7, "&lt;!&gt;", "starbase");
	}
	
	
	@Test
	public void test_mark_navigatable_sectors() {
		Quadrant quadrant = new Quadrant("q 1 2", 1, 2);
		starMap.setQuadrant(quadrant);
		enterprise.setQuadrant(quadrant);
		enterprise.setLocation(Location.location(4, 4));
		quadrant.add(new Star(1,6,StarClass.A));
		quadrant.add(new Star(2,6,StarClass.A));
		quadrant.add(new Star(3,6,StarClass.A));
		quadrant.add(new Star(5,6,StarClass.A));
		quadrant.add(new Star(6,6,StarClass.A));
		quadrant.add(new Star(7,6,StarClass.A));
		quadrant.add(new Star(4,3,StarClass.A));
		List<Location> targets = enterprise.findReachableSectors();
		assertFalse(targets.isEmpty());
		presenter.updateMapWithReachableSectors();
		for (Location loc:targets)
			verify(view).addCssToCell(loc.x, loc.y, "navigation-target");
		for (Star star:quadrant.getStars())
			verify(view, never()).addCssToCell(star.getLocation().x, star.getLocation().y, "navigation-target");
	}
	
	@Test
	public void test_thingMoved() {
		Quadrant q = new Quadrant("test", 1, 2);
		Klingon k = new Klingon(Klingon.ShipClass.BirdOfPrey);
		k.setLocation(Location.location(3, 4));
		q.add(k);
		presenter.thingMoved(k, q, Location.location(2, 2), q, k.getLocation());
		verify(view).clearSector(2, 2);
	}
	
	@Test
	public void test_onSectorSelected() {
		SectorSelectedHandler handler = mock(SectorSelectedHandler.class);
		bus.addHandler(Events.SECTOR_SELECTED, handler);
		presenter.onSectorSelected(1, 2, 100, 200);
		verify(handler).onSectorSelected(Location.location(1, 2), quadrant, 100, 200);
	}

	@Test
	public void test_onKeyPress() {
		SectorSelectedHandler handler = mock(SectorSelectedHandler.class);
		bus.addHandler(Events.SECTOR_SELECTED, handler);
		
		when(view.isVisible()).thenReturn(true);
		when(view.getHorizontalOffsetOfSector(2, 1)).thenReturn(200);
		when(view.getVerticalOffsetOfSector(2, 1)).thenReturn(100);

		presenter.onKeyPressed(KeyCodes.KEY_RIGHT);
		presenter.onKeyPressed(KeyCodes.KEY_RIGHT);
		presenter.onKeyPressed(KeyCodes.KEY_DOWN);
		presenter.onKeyPressed('m');
		verify(handler).onSectorSelected(Location.location(2, 1), quadrant, 200, 100);
	}
}
