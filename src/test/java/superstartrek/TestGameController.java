package superstartrek;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import superstartrek.client.control.GameController;
import superstartrek.client.control.ScoreKeeper;
import superstartrek.client.control.ScoreKeeperImpl;
import superstartrek.client.eventbus.Events;
import superstartrek.client.space.Location;
import superstartrek.client.space.Star;
import superstartrek.client.space.Star.StarClass;
import superstartrek.client.vessels.Klingon;
import superstartrek.client.vessels.Weapon;
import superstartrek.client.vessels.Klingon.ShipClass;

public class TestGameController extends BaseTest {

	GameController controller;
	ScoreKeeper scoreKeeper = new ScoreKeeperImpl();

	@Before
	public void setup() {
		controller = new GameController(application, scoreKeeper);
	}

	@Test
	public void test_that_turn_ends_after_enterprise_fires() {
		bus.fireEvent(Events.AFTER_FIRE, (h) -> h.afterFire(quadrant, enterprise,
				new Klingon(ShipClass.BirdOfPrey), Weapon.phaser, 1, false));
		assertEquals(1, bus.getFiredCount(Events.TURN_ENDED));
		assertEquals(1, bus.getFiredCount(Events.KLINGON_TURN_STARTED));
		assertEquals(1, bus.getFiredCount(Events.TURN_STARTED));
	}

	@Test
	public void test_that_message_is_printed_when_star_is_hit() {
		bus.fireEvent(Events.AFTER_FIRE, (h) -> h.afterFire(quadrant, enterprise,
				new Star(1, 2, StarClass.A), Weapon.phaser, 1, false));
		assertEquals(1, bus.getFiredCount(Events.MESSAGE_POSTED));
	}

	@Test
	public void test_that_game_is_over_if_enterprise_destroyed() {
		enterprise.getShields().setValue(0);
		bus.fireEvent(Events.AFTER_FIRE,
				(h) -> h.afterFire(quadrant, enterprise, enterprise, Weapon.disruptor, 1, false));
		assertEquals(1, bus.getFiredCount(Events.GAME_OVER));
	}

	@Test
	public void test_that_turn_ends_after_enterprise_moves() {
		bus.fireEvent(Events.THING_MOVED, (h) -> h.thingMoved(enterprise, quadrant, enterprise.getLocation(), quadrant, Location.location(4, 4)));
		assertEquals(1, bus.getFiredCount(Events.TURN_ENDED));
	}

	@Test
	public void test_that_game_ends_after_last_klingon_is_destroyed() {
		Klingon k1 = new Klingon(ShipClass.BirdOfPrey);
		k1.setLocation(Location.location(4, 4));
		k1.getDisruptor().damageAndTurnOff(starMap.getStarDate());
		k1.getImpulse().damageAndTurnOff(starMap.getStarDate());
		quadrant.add(k1);
		Klingon k2 = new Klingon(ShipClass.BirdOfPrey);
		k2.setLocation(Location.location(6, 4));
		k2.getDisruptor().damageAndTurnOff(starMap.getStarDate());
		k2.getImpulse().damageAndTurnOff(starMap.getStarDate());
		quadrant.add(k2);

		k1.destroy();
		assertEquals(0, bus.getFiredCount(Events.GAME_OVER));

		k2.destroy();
		assertEquals(1, bus.getFiredCount(Events.GAME_OVER));
		assertEquals(4, bus.getFiredCount(Events.MESSAGE_POSTED));
	}

}
