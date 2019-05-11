package superstartrek;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import superstartrek.client.activities.combat.FireHandler.FireEvent;
import superstartrek.client.activities.combat.FireHandler.FireEvent.Phase;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.Klingon.ShipClass;
import superstartrek.client.activities.messages.MessageHandler.MessagePostedEvent;
import superstartrek.client.activities.navigation.ThingMovedHandler.ThingMovedEvent;
import superstartrek.client.control.GameController;
import superstartrek.client.control.GameOverEvent;
import superstartrek.client.control.KlingonTurnEndedEvent;
import superstartrek.client.control.KlingonTurnStartedEvent;
import superstartrek.client.control.ScoreKeeper;
import superstartrek.client.control.ScoreKeeperImpl;
import superstartrek.client.control.TurnEndedEvent;
import superstartrek.client.control.TurnStartedEvent;
import superstartrek.client.model.Location;
import superstartrek.client.model.Star;
import superstartrek.client.model.Star.StarClass;
import superstartrek.client.activities.navigation.EnterpriseRepairedHandler.EnterpriseRepairedEvent;;

public class TestGameController extends BaseTest{

	GameController controller;
	ScoreKeeper scoreKeeper = new ScoreKeeperImpl();
	
	@Before
	public void setup() {
		controller = new GameController(application, scoreKeeper);
	}
	
	@Test
	public void test_that_turn_ends_after_enterprise_fires() {
		FireEvent evt = new FireEvent(Phase.afterFire, quadrant, enterprise, new Klingon(ShipClass.BirdOfPrey), "Phaser", 1, false);
		events.fireEvent(evt);
		assertEquals(1, events.getFiredCount(TurnEndedEvent.TYPE));
		assertEquals(1, events.getFiredCount(KlingonTurnStartedEvent.TYPE));
		assertEquals(1, events.getFiredCount(KlingonTurnEndedEvent.TYPE));
		assertEquals(1, events.getFiredCount(TurnStartedEvent.TYPE));
	}

	@Test
	public void test_that_message_is_printed_when_star_is_hit() {
		FireEvent evt = new FireEvent(Phase.afterFire, quadrant, enterprise, new Star(1,2,StarClass.A), "Phaser", 1, false);
		events.fireEvent(evt);
		assertEquals(1, events.getFiredCount(MessagePostedEvent.TYPE));
	}

	@Test
	public void test_that_game_is_over_if_enterprise_destroyed() {
		enterprise.getShields().setValue(0);
		FireEvent evt = new FireEvent(Phase.afterFire, quadrant, enterprise, enterprise, "disruptor", 1, false);
		events.fireEvent(evt);
		assertEquals(1, events.getFiredCount(GameOverEvent.TYPE));
	}

	@Test
	public void test_that_turn_ends_after_enterprise_repairs() {
		enterprise.getShields().setValue(0);
		EnterpriseRepairedEvent evt = new EnterpriseRepairedEvent(enterprise);
		events.fireEvent(evt);
		assertEquals(1, events.getFiredCount(TurnEndedEvent.TYPE));
	}

	@Test
	public void test_that_turn_ends_after_enterprise_moves() {
		ThingMovedEvent evt = new ThingMovedEvent(enterprise, quadrant, enterprise.getLocation(), quadrant, Location.location(4, 4));
		events.fireEvent(evt);
		assertEquals(1, events.getFiredCount(TurnEndedEvent.TYPE));
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
		assertEquals(0, events.getFiredCount(GameOverEvent.TYPE));

		k2.destroy();
		assertEquals(1, events.getFiredCount(GameOverEvent.TYPE));
		assertEquals(4, events.getFiredCount(MessagePostedEvent.TYPE));
	}

}
