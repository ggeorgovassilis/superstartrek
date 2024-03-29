package superstartrek;

import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.shared.testing.CountingEventBus;

import superstartrek.client.Application;
import superstartrek.client.activities.computer.srs.SRSView;
import superstartrek.client.activities.computer.srs.SRSPresenter;
import superstartrek.client.space.Location;
import superstartrek.client.space.Quadrant;
import superstartrek.client.space.Star;
import superstartrek.client.space.StarBase;
import superstartrek.client.space.StarMap;
import superstartrek.client.space.Star.StarClass;
import superstartrek.client.vessels.Enterprise;

public class TestSRSPresenter {

	SRSPresenter presenter;
	Application app;
	CountingEventBus events;
	StarMap map;
	SRSView view;
	
	@Before
	public void setup() {
		app = new Application();
		app.starMap = new StarMap();
		presenter = new SRSPresenter();
		view = mock(SRSView.class);
		presenter.setView(view);
	}
	
	@After
	public void cleanup() {
		Application.set(null);
	}


	Quadrant makeQuadrant(int x, int y, boolean hasStarBase, int stars, int klingons) {
		Quadrant q = new Quadrant("test quadrant", x, y);
		if (hasStarBase) 
			q.setStarBase(new StarBase(Location.location(0,0)));
		for (int i=0;i<stars;i++) {
			q.add(new Star(Location.location(i, 1), StarClass.A));
		}
		for (int i=0;i<klingons;i++) {
			q.add(new Star(Location.location(i, 2), StarClass.A));
		}
		return q;
	}
	
	@Test
	public void test() {
		app.starMap = map = new StarMap();
		map.enterprise = new Enterprise(app, map);
		map.setQuadrant(makeQuadrant(0,0, true, 0, 0));
		map.setQuadrant(makeQuadrant(1,0, false, 0, 1));
		map.setQuadrant(makeQuadrant(2,0, false, 0, 2));
		
		map.setQuadrant(makeQuadrant(0,1, false, 1, 0));
		map.setQuadrant(makeQuadrant(1,1, true, 3, 1));
		map.setQuadrant(makeQuadrant(2,1, false, 4, 2));

		map.setQuadrant(makeQuadrant(0,2, true, 3, 0));
		map.setQuadrant(makeQuadrant(1,2, false, 2, 1));
		map.setQuadrant(makeQuadrant(2,2, true, 1, 2));
		Quadrant q = map.getQuadrant(1, 1);
		Enterprise e = map.enterprise = new Enterprise(app, map);
		e.setQuadrant(q);
		e.setLocation(Location.location(6,7));
		presenter.updateRadar();
		verify(view).updateCell(0, 0, "!0", " has-starbase");
		verify(view).updateCell(1, 0, "1", "");
		verify(view).updateCell(2, 0, "2", "");

		verify(view).updateCell(0, 1, "1", "");
		verify(view).updateCell(1, 1, "!4", "has-enterprise has-starbase");
		verify(view).updateCell(2, 1, "6", "");

		verify(view).updateCell(0, 2, "!3", " has-starbase");
		verify(view).updateCell(1, 2, "3", "");
		verify(view).updateCell(2, 2, "!3", " has-starbase");
	}
}
