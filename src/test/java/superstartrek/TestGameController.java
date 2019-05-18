package superstartrek;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import superstartrek.client.activities.combat.FireHandler;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.Klingon.ShipClass;
import superstartrek.client.activities.pwa.Callback;
import superstartrek.client.bus.Events;
import superstartrek.client.control.GameController;
import superstartrek.client.control.ScoreKeeper;
import superstartrek.client.control.ScoreKeeperImpl;
import superstartrek.client.model.Location;
import superstartrek.client.model.Star;
import superstartrek.client.model.Star.StarClass;
import superstartrek.client.activities.navigation.EnterpriseRepairedHandler;
import superstartrek.client.activities.navigation.ThingMovedHandler;

public class TestGameController extends BaseTest {

	GameController controller;
	ScoreKeeper scoreKeeper = new ScoreKeeperImpl();

	@Before
	public void setup() {
		controller = new GameController(application, scoreKeeper);
	}

	@Test
	public void test_that_turn_ends_after_enterprise_fires() {
		bus.invoke(Events.AFTER_FIRE, (Callback<FireHandler>) (h) -> h.afterFire(quadrant, enterprise,
				new Klingon(ShipClass.BirdOfPrey), "Phaser", 1, false));
		assertEquals(1, bus.getFiredCount(Events.TURN_ENDED));
		assertEquals(1, bus.getFiredCount(Events.KLINGON_TURN_STARTED));
		assertEquals(1, bus.getFiredCount(Events.KLINGON_TURN_ENDED));
		assertEquals(1, bus.getFiredCount(Events.TURN_STARTED));
	}

	@Test
	public void test_that_message_is_printed_when_star_is_hit() {
		bus.invoke(Events.AFTER_FIRE, (Callback<FireHandler>) (h) -> h.afterFire(quadrant, enterprise,
				new Star(1, 2, StarClass.A), "Phaser", 1, false));
		assertEquals(1, bus.getFiredCount(Events.MESSAGE_POSTED));
	}

	@Test
	public void test_that_game_is_over_if_enterprise_destroyed() {
		enterprise.getShields().setValue(0);
		bus.invoke(Events.AFTER_FIRE,
				(Callback<FireHandler>) (h) -> h.afterFire(quadrant, enterprise, enterprise, "disruptor", 1, false));
		assertEquals(1, bus.getFiredCount(Events.GAME_OVER));
	}

	@Test
	public void test_that_turn_ends_after_enterprise_repairs() {
		enterprise.getShields().setValue(0);
		bus.invoke(Events.ENTERPRISE_REPAIRED,
				(Callback<EnterpriseRepairedHandler>) (h) -> h.onEnterpriseRepaired(enterprise));
		assertEquals(1, bus.getFiredCount(Events.TURN_ENDED));
	}

	@Test
	public void test_that_turn_ends_after_enterprise_moves() {
		bus.invoke(Events.THING_MOVED, (Callback<ThingMovedHandler>) (h) -> h.thingMoved(enterprise, quadrant, enterprise.getLocation(), quadrant, Location.location(4, 4)));
		assertEquals(1, bus.getFiredCount(Events.TURN_ENDED));
	}

	@Test
	public void test_that_game_ends_after_last_klingon_is_destroyed() {
		Klingon k1 = new Klingon(ShipClass.BirdOfPrey);
		k1.setLocation(Location.location(4, 4));
		k1.getDisruptor().disable();
		k1.getImpulse().disable();
		quadrant.getKlingons().add(k1);
		Klingon k2 = new Klingon(ShipClass.BirdOfPrey);
		k2.setLocation(Location.location(6, 4));
		k2.getDisruptor().disable();
		k2.getImpulse().disable();
		quadrant.getKlingons().add(k2);

		k1.destroy();
		assertEquals(0, bus.getFiredCount(Events.GAME_OVER));

		k2.destroy();
		assertEquals(1, bus.getFiredCount(Events.GAME_OVER));
		assertEquals(3, bus.getFiredCount(Events.MESSAGE_POSTED));
	}

}
