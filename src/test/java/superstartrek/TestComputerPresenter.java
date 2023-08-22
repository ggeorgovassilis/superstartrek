package superstartrek;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import superstartrek.client.activities.computer.ComputerPresenter;
import superstartrek.client.activities.computer.IComputerScreen;
import superstartrek.client.control.ScoreKeeper;
import superstartrek.client.space.Location;
import superstartrek.client.space.Star;
import superstartrek.client.space.StarBase;
import superstartrek.client.space.Star.StarClass;
import superstartrek.client.vessels.Klingon;
import superstartrek.client.vessels.Klingon.ShipClass;

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
	public void testOnPlayerTurnStarted_1() {
		presenter.onPlayerTurnStarted();

		verify(view).showStarDate("2100");
		verify(view).setQuadrantName("test quadrant 1:2", "");
		verify(view).updateAntimatter(1000,1000);

	}

	@Test
	public void testOnPlayerTurnStarted_2() {
		enterprise.setLocation(Location.location(1, 1));
		enterprise.getPhasers().damage(10, starMap.getStarDate());
		quadrant.setStarBase(new StarBase(Location.location(3, 3)));
		presenter.onPlayerTurnStarted();
		verify(view).showStarDate("2100");
		verify(view).setQuadrantName("test quadrant 1:2", "");
		verify(view).updateAntimatter(1000,1000);
		verify(view).updateShields(60, 60, 60);
	}


	@Test
	public void test_updateQuadrantHeader_klingon_near() {
		enterprise.setLocation(Location.location(1, 1));
		Klingon k = new Klingon(ShipClass.Raider);
		k.setLocation(Location.location(3, 3));
		quadrant.add(k);
		
		presenter.updateQuadrantHeaderView();
		
		verify(view).setQuadrantName("test quadrant 1:2", "red-alert");
	}

	@Test
	public void test_updateQuadrantHeader_klingon_far() {
		enterprise.setLocation(Location.location(1, 1));
		Klingon k = new Klingon(ShipClass.Raider);
		k.setLocation(Location.location(5, 7));
		quadrant.add(k);
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
		verify(view).updateShields(50, 50, 60);
	}

	@Test
	public void test_statusButtonView() {
		enterprise.getTorpedos().damageAndTurnOff(starMap.getStarDate());
		enterprise.getPhasers().damage(10, starMap.getStarDate());
		presenter.updateStatusButtonView();
		verify(view).updateShortStatus("", "", "damaged damage-medium", "damage-offline");
	}
	
	@Test
	public void test_toggle_shields_direction() {
		presenter.onToggleShieldsButtonClicked();
		verify(view).addShieldCss("shield-omni");
	}
	
	@Test
	public void test_scanSector_empty_sector() {
		presenter.scanSector(Location.location(6, 6), quadrant);
		verify(view).setScanProperty("scan-report-name", "scan-report-name-value", "", "Nothing at 6:6");
		verify(view).setScanProperty("scan-report-shields", "scan-report-shields-value", "hidden", "");
		verify(view).setScanProperty("scan-report-weapons", "scan-report-weapons-value", "hidden","");
		verify(view).setScanProperty("scan-report-cloak", "scan-report-cloak-value", "hidden","");
		verify(view).setScanProperty("scan-report-engines", "scan-report-engines-value", "hidden","");
		verify(view).show();

	}

	@Test
	public void test_scanSector_cloaked_klingon() {
		Klingon k = new Klingon(ShipClass.Raider);
		k.cloak();
		k.setLocation(Location.location(4, 4));
		quadrant.add(k);
		presenter.scanSector(Location.location(4, 4), quadrant);
		verify(view).setScanProperty("scan-report-name", "scan-report-name-value", "", "Nothing at 4:4");
		verify(view).setScanProperty("scan-report-shields", "scan-report-shields-value", "hidden", "");
		verify(view).setScanProperty("scan-report-weapons", "scan-report-weapons-value", "hidden","");
		verify(view).setScanProperty("scan-report-cloak", "scan-report-cloak-value", "hidden","");
		verify(view).setScanProperty("scan-report-engines", "scan-report-engines-value", "hidden","");
	}

	@Test
	public void test_scanSector_klingon() {
		Klingon k = new Klingon(ShipClass.Raider);
		k.uncloak();
		k.setLocation(Location.location(4, 4));
		quadrant.add(k);
		presenter.scanSector(Location.location(4, 4), quadrant);
		verify(view).setScanProperty("scan-report-name", "scan-report-name-value", "", "a Klingon raider at 4:4");
		verify(view).setScanProperty("scan-report-shields", "scan-report-shields-value", "", "%100");
		verify(view).setScanProperty("scan-report-weapons", "scan-report-weapons-value", "","online");
		verify(view).setScanProperty("scan-report-cloak", "scan-report-cloak-value", "","online");
		verify(view).setScanProperty("scan-report-engines", "scan-report-engines-value", "","online");
	}

	@Test
	public void test_scanSector_star() {
		quadrant.add(new Star(3,3, StarClass.B));
		presenter.scanSector(Location.location(3, 3), quadrant);
		verify(view).setScanProperty("scan-report-name", "scan-report-name-value", "", "Class B star at 3:3");
		verify(view).setScanProperty("scan-report-shields", "scan-report-shields-value", "hidden", "");
		verify(view).setScanProperty("scan-report-weapons", "scan-report-weapons-value", "hidden","");
		verify(view).setScanProperty("scan-report-cloak", "scan-report-cloak-value", "hidden","");
		verify(view).setScanProperty("scan-report-engines", "scan-report-engines-value", "hidden","");
	}

}
