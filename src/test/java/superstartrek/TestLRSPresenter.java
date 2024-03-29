package superstartrek;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import superstartrek.client.Application;
import superstartrek.client.activities.lrs.LRSScreen;
import superstartrek.client.activities.lrs.LRSPresenter;
import superstartrek.client.space.Constants;
import superstartrek.client.space.Location;
import superstartrek.client.space.Quadrant;
import superstartrek.client.space.Star;
import superstartrek.client.space.Star.StarClass;
import superstartrek.client.vessels.Klingon;
import superstartrek.client.vessels.Klingon.ShipClass;

import static org.mockito.Mockito.*;

public class TestLRSPresenter extends BaseTest{

	LRSPresenter presenter;
	LRSScreen view;
	
	@Before
	public void setup() {
		presenter = new LRSPresenter();
		view = mock(LRSScreen.class);
		presenter.setView(view);
	}
	
	@After
	public void cleanup() {
		Application.set(null);
	}
	
	@Test
	public void test_showLrs() {
		
		for (int x=0;x<Constants.SECTORS_EDGE;x++)
		for (int y=0;y<Constants.SECTORS_EDGE;y++) {
			Quadrant q = new Quadrant("q"+x+""+y, x, y);
			if ((x+y) % 5 == 0) {
				for (int i=0;i<y;i++) {
					Klingon k = new Klingon(ShipClass.BirdOfPrey);
					q.add(k);
				}
			}
			for (int i=0;i<(x+y)%(Constants.SECTORS_EDGE-1);i++) {
				Star star = new Star(Location.location(x, y), StarClass.A);
				q.add(star);
			}
			if (x<3&&y<3)
				q.setExplored(true);
			starMap.setQuadrant(q);
			
		}
		enterprise.setQuadrant(starMap.getQuadrant(0, 0));
		presenter.showLrs();
		verify(view).show();
		verify(view).updateCell(0, 0, "0", "navigation-target has-enterprise explored");
		verify(view).updateCell(1, 0, "1", "navigation-target  explored");
		verify(view).updateCell(0, 1, "1", "navigation-target  explored");
		verify(view).updateCell(2, 0, "2", "navigation-target  explored");
		verify(view).updateCell(eq(3), eq(0), eq("3"), eq("navigation-target "));
	}
}
