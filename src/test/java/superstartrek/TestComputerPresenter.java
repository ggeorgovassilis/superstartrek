package superstartrek;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import superstartrek.client.activities.computer.ComputerPresenter;
import superstartrek.client.activities.computer.IComputerScreen;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.Klingon.ShipClass;
import superstartrek.client.control.ScoreKeeper;
import superstartrek.client.model.Location;
import superstartrek.client.model.StarBase;

public class TestComputerPresenter extends BaseTest{

	ComputerPresenter presenter;
	IComputerScreen view;
	ScoreKeeper scoreKeeper;

	@Before
	public void setup() {
		scoreKeeper = mock(ScoreKeeper.class);
		presenter = new ComputerPresenter(application, scoreKeeper);
		view = mock(IComputerScreen.class);
		presenter.setView(view);
		presenter.setEnterprise(enterprise);
		presenter.setStarMap(starMap);
	}
	
	@Test
	public void testOnTurnStarted_1() {
		presenter.onTurnStarted();

		verify(view).showStarDate("2100");
		verify(view).setQuadrantName("test quadrant 1:2", "");
		verify(view).updateAntimatter(1000,1000);

	}

	@Test
	public void testOnTurnStarted_2() {
		enterprise.setLocation(Location.location(1, 1));
		enterprise.getPhasers().damage(10, starMap.getStarDate());
		quadrant.setStarBase(new StarBase(Location.location(3, 3)));
		presenter.onTurnStarted();

		verify(view).showStarDate("2100");
		verify(view).setQuadrantName("test quadrant 1:2", "");
		verify(view).updateAntimatter(1000,1000);
		verify(view).updateShields(100, 100, 100);
	}


	@Test
	public void test_updateQuadrantHeader_klingon_near() {
		enterprise.setLocation(Location.location(1, 1));
		Klingon k = new Klingon(ShipClass.Raider);
		k.setLocation(Location.location(3, 3));
		quadrant.getKlingons().add(k);
		
		presenter.updateQuadrantHeaderView();
		
		verify(view).setQuadrantName("test quadrant 1:2", "red-alert");
	}

	@Test
	public void test_updateQuadrantHeader_klingon_far() {
		enterprise.setLocation(Location.location(1, 1));
		Klingon k = new Klingon(ShipClass.Raider);
		k.setLocation(Location.location(5, 7));
		quadrant.getKlingons().add(k);
		presenter.updateQuadrantHeaderView();
		
		verify(view).setQuadrantName("test quadrant 1:2", "yellow-alert");
	}
	
	@Test
	public void test_updateQuadrantHeader() {
		enterprise.setLocation(Location.location(1, 1));
		presenter.updateQuadrantHeaderView();
		
		verify(view).setQuadrantName("test quadrant 1:2", "");
	}
	
	@Test
	public void test_updateShieldsView() {
		enterprise.getShields().damage(10, starMap.getStarDate());
		presenter.updateShieldsView();
		verify(view).updateShields(90, 90, 100);
	}

	@Test
	public void test_statusButtonView() {
		enterprise.getTorpedos().setEnabled(false);
		enterprise.getPhasers().damage(10, starMap.getStarDate());
		presenter.updateStatusButtonView();
		verify(view).updateShortStatus("", "", "damaged damage-medium", "damaged damage-offline");
	}
	

}
