package superstartrek;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import superstartrek.client.Application;
import superstartrek.client.activities.computer.quadrantscanner.QuadrantScannerView;
import superstartrek.client.activities.computer.sectorcontextmenu.SectorContextMenuPresenter;
import superstartrek.client.activities.computer.sectorcontextmenu.SectorSelectedHandler;
import superstartrek.client.activities.computer.quadrantscanner.QuadrantScannerPresenter;
import superstartrek.client.eventbus.Events;
import superstartrek.client.space.Location;
import superstartrek.client.space.Quadrant;
import superstartrek.client.space.Star;
import superstartrek.client.space.StarBase;
import superstartrek.client.space.Star.StarClass;
import superstartrek.client.vessels.Klingon;
import superstartrek.client.vessels.Klingon.ShipClass;

public class TestQuadrantScannerPresenter extends BaseTest{

	QuadrantScannerPresenter presenter;
	SectorContextMenuPresenter sectorMenuPresenter;
	QuadrantScannerView view;
	
	@Before
	public void setup() {
		sectorMenuPresenter = mock(SectorContextMenuPresenter.class);
		presenter = new QuadrantScannerPresenter(sectorMenuPresenter);
		view = mock(QuadrantScannerView.class);
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
		
		qTo.add(new Star(Location.location(6,5), StarClass.A));
		qTo.add(new Star(Location.location(5,6), StarClass.A));
		
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
		quadrant.add(new Star(Location.location(1,6),StarClass.A));
		quadrant.add(new Star(Location.location(2,6),StarClass.A));
		quadrant.add(new Star(Location.location(3,6),StarClass.A));
		quadrant.add(new Star(Location.location(5,6),StarClass.A));
		quadrant.add(new Star(Location.location(6,6),StarClass.A));
		quadrant.add(new Star(Location.location(7,6),StarClass.A));
		quadrant.add(new Star(Location.location(4,3),StarClass.A));
		enterprise.setQuadrant(quadrant);
		enterprise.setLocation(Location.location(4, 4));
		enterprise.updateReachableSectors();
		enterprise.setLocation(Location.location(5, 4));
		List<Location> targets = enterprise.getLastReachableSectors();
		assertFalse(targets.isEmpty());
		presenter.updateMapWithReachableSectors();
		Location[] expectedRemoves = {Location.location(2, 2), Location.location(2, 3), Location.location(5, 4),
				Location.location(1, 4), Location.location(2, 5), Location.location(4, 7)};
		Location[] expectedAdds = {Location.location(5, 2), Location.location(5, 1),
				Location.location(4, 2), Location.location(7, 2), Location.location(4, 4), Location.location(7, 3),
				Location.location(7, 5)};
		for (Location l:expectedRemoves)
			verify(view).removeCssFromCell(l.x, l.y, "navigation-target");
		for (Location l:expectedAdds)
			verify(view).addCssToCell(l.x, l.y, "navigation-target");
		for (Star star:quadrant.getStars())
			verify(view, never()).addCssToCell(star.getLocation().x, star.getLocation().y, "navigation-target");
		verifyNoMoreInteractions(view);
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

}
