package superstartrek.client.control;

import com.google.gwt.event.shared.EventBus;
import superstartrek.client.Application;
import superstartrek.client.activities.combat.FireHandler;
import superstartrek.client.activities.computer.EnergyConsumptionHandler;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.KlingonDestroyedHandler;
import superstartrek.client.activities.klingons.Klingon.ShipClass;
import superstartrek.client.activities.messages.MessageHandler;
import superstartrek.client.activities.navigation.EnterpriseDockedHandler;
import superstartrek.client.activities.navigation.EnterpriseRepairedHandler;
import superstartrek.client.activities.navigation.ThingMovedHandler;
import superstartrek.client.activities.pwa.Callback;
import superstartrek.client.bus.Bus;
import superstartrek.client.bus.Events;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Star;
import superstartrek.client.model.StarBase;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;

public class GameController implements GamePhaseHandler, FireHandler, EnterpriseRepairedHandler, ThingMovedHandler,
		KlingonDestroyedHandler, MessageHandler, EnergyConsumptionHandler, EnterpriseDockedHandler {

	Application application;
	EventBus events;
	Bus bus;
	boolean gameIsRunning = true;
	boolean startTurnPending = false;
	boolean endTurnPending = false;
	ScoreKeeper scoreKeeper;

	public GameController(Application application, ScoreKeeper scoreKeeper) {
		this.application = application;
		events = application.events;
		bus = application.bus;
		this.scoreKeeper = scoreKeeper;
		bus.register(Events.GAME_STARTED, this);
		bus.register(Events.GAME_OVER, this);
		bus.register(Events.TURN_STARTED, this);
		bus.register(Events.TURN_ENDED, this);
		bus.register(Events.KLINGON_TURN_STARTED, this);
		bus.register(Events.AFTER_FIRE, this);
		bus.register(Events.ENTERPRISE_REPAIRED, this);
		bus.register(Events.THING_MOVED, this);
		bus.register(Events.KLINGON_DESTROYED, this);
		bus.register(Events.MESSAGE_READ, this);
		bus.register(Events.TURN_YIELDED, this);
		bus.register(Events.CONSUME_ENERGY, this);
		bus.register(Events.ENTERPRISE_DOCKED, this);
		bus.register(Events.GAME_RESTART, this);
	}

	public ScoreKeeper getScoreKeeper() {
		return scoreKeeper;
	}

	@Override
	public void afterFire(Quadrant quadrant, Vessel actor, Thing target, String weapon, double damage, boolean wasAutoFire) {
		if (Enterprise.is(target)) {
			Enterprise enterprise = target.as();
			if (enterprise.getShields().getValue() <= 0)
				gameOver(GameOutcome.lost, "shields");

		} else if (Star.is(target)) {
			Star star = target.as();
			application.message(weapon + " hit " + star.getName() + " at " + star.getLocation());
		}
		if (actor == application.starMap.enterprise && !wasAutoFire)
			endTurnAfterThis();
	}

	@Override
	public void onEnterpriseRepaired(Enterprise enterprise) {
		endTurnAfterThis();
		getScoreKeeper().addScore(ScoreKeeper.POINTS_ENTERPRISE_REPAIR);
	}

	@Override
	public void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
		if (thing == application.starMap.enterprise)
			endTurnAfterThis();
	}

	@Override
	public void onKlingonDestroyed(Klingon klingon) {

		getScoreKeeper().addScore(klingon.shipClass == ShipClass.Raider ? ScoreKeeper.POINTS_KLINGON_RAIDER_DESTROYED
				: ScoreKeeper.POINTS_KLINGON_BOF_DESTROYED);
		if (!application.starMap.hasKlingons())
			bus.invoke(Events.GAME_OVER, (Callback<GamePhaseHandler>)(h)->h.gameWon());
	}

	@Override
	public void gameOver() {
		application.message("Game over.");
		gameIsRunning = false;
	}

	@Override
	public void gameWon() {
		getScoreKeeper().addScore(ScoreKeeper.POINTS_GAME_WON);
		application.message("Congratulations, all Klingons were destroyed.", "gamewon");
	}

	@Override
	public void gameLost() {
		getScoreKeeper().addScore(ScoreKeeper.POINTS_ENTERPRISE_DESTROYED);
		application.message("The Enterprise was destroyed.", "gameover");
	}

	@Override
	public void messagesAcknowledged() {
		// TODO: this is too implicit. The intention is that, once the game has been
		// lost/won and the user clicks away the informing message, the game should
		// reload.
		if (!this.gameIsRunning)
			application.restart();
	}

	public void startTurn() {
		getScoreKeeper().addScore(ScoreKeeper.POINTS_DAY);
		application.starMap.advanceStarDate(1);
		bus.invoke(Events.TURN_STARTED, (Callback<GamePhaseHandler>)(h)->h.onTurnStarted());
		bus.invoke(Events.AFTER_TURN_STARTED, (Callback<GamePhaseHandler>)(h)->h.afterTurnStarted());
	}

	public void startTurnAfterThis() {
		if (startTurnPending)
			return;
		startTurnPending = true;
		superstartrek.client.utils.Timer.postpone(() -> {
			startTurnPending = false;
			startTurn();
		});
	}

	public void startGame() {
		application.browserAPI.postHistoryChange("intro", true);
		application.bus.invoke(Events.GAME_STARTED, (Callback<GamePhaseHandler>)(h)->h.onGameStarted(application.starMap));
	}

	public void endTurn() {
		bus.invoke(Events.TURN_ENDED, (Callback<GamePhaseHandler>)(h)->h.onTurnEnded());
		bus.invoke(Events.KLINGON_TURN_STARTED, (Callback<GamePhaseHandler>)(h)->h.onKlingonTurnStarted());
		bus.invoke(Events.KLINGON_TURN_ENDED, (Callback<GamePhaseHandler>)(h)->h.onKlingonTurnEnded());
		// release resources so that it can be (hopefully) garbage collected; at this
		// point, everyone who needs resources should have them
	}

	@Override
	public void onTurnYielded() {
		endTurnAfterThis();
	}

	public void endTurnAfterThis() {
		if (endTurnPending)
			return;
		endTurnPending = true;
		superstartrek.client.utils.Timer.postpone(() -> {
			endTurnPending = false;
			endTurn();
			startTurnAfterThis();
		});
	}

	public void gameOver(GameOutcome outcome, String reason) {
		if (outcome == GameOutcome.won)
			bus.invoke(Events.GAME_OVER, (Callback<GamePhaseHandler>)(h)->h.gameWon());
		if (outcome == GameOutcome.lost)
			bus.invoke(Events.GAME_OVER, (Callback<GamePhaseHandler>)(h)->h.gameLost());
	}

	@Override
	public void handleEnergyConsumption(Thing consumer, double value, String type) {
		if (Enterprise.is(consumer)) {
			Enterprise enterprise = consumer.as();
			if (enterprise.getAntimatter().getValue() <= 0) {
				application.message("We run out of anti matter");
				gameOver(GameOutcome.lost, "We run out of anti matter");
			}
		}
	}

	@Override
	public void onEnterpriseDocked(Enterprise enterprise, StarBase starBase) {
		getScoreKeeper().addScore(ScoreKeeper.POINTS_DOCK_STARBASE);
	}
	
	@Override
	public void beforeGameRestart() {
		scoreKeeper.reset();
		gameIsRunning = true;
	}

}
