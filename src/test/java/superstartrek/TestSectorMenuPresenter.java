package superstartrek;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.shared.testing.CountingEventBus;

import superstartrek.client.Application;
import superstartrek.client.activities.sector.contextmenu.ISectorMenuView;
import superstartrek.client.activities.sector.contextmenu.SectorMenuPresenter;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;

public class TestSectorMenuPresenter {

	SectorMenuPresenter presenter;
	Application app;
	CountingEventBus events;
	StarMap map;
	ISectorMenuView view;
	
	@Before
	public void setup() {
		app = Application.get();
		app.events = events = new CountingEventBus();
		app.starMap = new StarMap();
		presenter = new SectorMenuPresenter(app);
		view = mock(ISectorMenuView.class);
		presenter.setView(view);
	}

	@Test
	public void test() {
		app.starMap = map = new StarMap();
		Quadrant q = new Quadrant("test q", 3, 4);
		Enterprise e = new Enterprise(app);
		e.setQuadrant(q);
		map.enterprise = e;
		presenter.showMenu(77, Location.location(1,2), q);
		verify(view).enableButton("cmd_navigate", true);
		verify(view).enableButton("cmd_firePhasers", true);
		verify(view).enableButton("cmd_fireTorpedos", true);
		verify(view).enableButton("cmd_toggleFireAtWill", true);
	}
}
