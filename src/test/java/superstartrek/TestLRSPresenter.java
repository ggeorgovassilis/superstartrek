package superstartrek;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.google.gwt.event.shared.testing.CountingEventBus;

import superstartrek.client.Application;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.Klingon.ShipClass;
import superstartrek.client.activities.lrs.ILRSScreen;
import superstartrek.client.activities.lrs.LRSPresenter;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Star;
import superstartrek.client.model.Star.StarClass;
import superstartrek.client.utils.BrowserAPI;
import superstartrek.client.model.StarMap;

import static org.mockito.Mockito.*;

public class TestLRSPresenter {

	LRSPresenter presenter;
	ILRSScreen view;
	Application app;
	StarMap map;
	CountingEventBus events;
	BrowserAPI browser;
	
	@Before
	public void setup() {
		app = new Application();
		map = new StarMap();
		app.browserAPI = browser = mock(BrowserAPI.class);
		app.events = events = new CountingEventBus();
		app.starMap = map;
		presenter = new LRSPresenter(app);
		view = mock(ILRSScreen.class);
		presenter.setView(view);
	}
	
	@After
	public void cleanup() {
		Application.set(null);
	}
	
	@Test
	public void test_showLrs() {
		
		Enterprise enterprise = new Enterprise(app, map);
		map.enterprise = enterprise;
		for (int x=0;x<8;x++)
		for (int y=0;y<8;y++) {
			Quadrant q = new Quadrant("q"+x+""+y, x, y);
			if ((x+y) % 5 == 0) {
				for (int i=0;i<y;i++) {
					Klingon k = new Klingon(ShipClass.BirdOfPrey);
					q.getKlingons().add(k);
				}
			}
			for (int i=0;i<(x+y)%7;i++) {
				Star star = new Star(x, y, StarClass.A);
				q.getStars().add(star);
			}
			if (x<3&&y<3)
				q.setExplored(true);
			map.setQuadrant(q);
			
		}
		enterprise.setQuadrant(map.getQuadrant(0, 0));
		presenter.showLrs();
		verify(view).show();
		for (int x=0;x<8;x++)
		for (int y=0;y<8;y++) {
			Quadrant q = map.getQuadrant(x, y);
			System.out.println(x+":"+y+" "+q.getKlingons().size()+" "+q.getStars().size());
			if (enterprise.getQuadrant() == q)
				verify(view).updateCell(0, 0, " 0", "has-enterprise explored");
			else
			if (x<3&&y<3)
			verify(view).updateCell(x, y, " "+q.getStars().size(), " explored");
			else 
				verify(view).updateCell(x, y, " "+q.getStars().size(), "");
		}
	}
}
