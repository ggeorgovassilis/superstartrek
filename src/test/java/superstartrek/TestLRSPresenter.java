package superstartrek;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.shared.testing.CountingEventBus;

import superstartrek.client.Application;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.Klingon.ShipClass;
import superstartrek.client.activities.lrs.ILRSScreen;
import superstartrek.client.activities.lrs.LRSPresenter;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Star;
import superstartrek.client.model.StarBase;
import superstartrek.client.model.StarMap;

public class TestLRSPresenter {

	LRSPresenter presenter;
	Application app;
	CountingEventBus events;
	StarMap map;
	ILRSScreen view;
	
	@Before
	public void setup() {
		app = Application.get();
		app.events = new CountingEventBus();
		presenter = new LRSPresenter(app);
		app.starMap = map = new StarMap();
		view = mock(ILRSScreen.class);
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
		
		Klingon k = new Klingon(app, ShipClass.BirdOfPrey);
		k.setLocation(Location.location(7,7));
		qTo.getKlingons().add(k);
		presenter.setView(view);
		
		presenter.onEnterpriseWarped(enterprise, qFrom, lFrom, qTo, lTo);
		verify(view).updateCell(eq(1), eq(2), eq(" 0"), eq(""));
		verify(view).updateCell(eq(3), eq(4), eq("!2"), eq("has-enterprise has-starbase"));
	}
}
