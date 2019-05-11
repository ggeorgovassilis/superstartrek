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
import superstartrek.client.control.GameOverEvent.Outcome;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Star;
import superstartrek.client.model.StarBase;
import superstartrek.client.model.Thing;

public class GameController implements GamePhaseHandler, FireHandler, EnterpriseRepairedHandler, ThingMovedHandler,
		KlingonDestroyedHandler, MessageHandler, EnergyConsumptionHandler, EnterpriseDockedHandler {

	Application application;
	EventBus events;
	boolean gameIsRunning = true;
	boolean startTurnPending = false;
	boolean endTurnPending = false;
	ScoreKeeper scoreKeeper;

	public GameController(Application application, ScoreKeeper scoreKeeper) {
		this.application = application;
		events = application.events;
		this.scoreKeeper = scoreKeeper;
		events.addHandler(GameStartedEvent.TYPE, this);
		events.addHandler(GameOverEvent.TYPE, this);
		events.addHandler(TurnStartedEvent.TYPE, this);
		events.addHandler(TurnEndedEvent.TYPE, this);
		events.addHandler(KlingonTurnStartedEvent.TYPE, this);
		events.addHandler(FireEvent.TYPE, this);
		events.addHandler(EnterpriseRepairedEvent.TYPE, this);
		events.addHandler(ThingMovedEvent.TYPE, this);
		events.addHandler(KlingonDestroyedEvent.TYPE, this);
		events.addHandler(MessagesReadEvent.TYPE, this);
		events.addHandler(YieldTurnEvent.TYPE, this);
		events.addHandler(EnergyConsumptionEvent.TYPE, this);
		events.addHandler(EnterpriseDockedEvent.TYPE, this);
	}

	public ScoreKeeper getScoreKeeper() {
		return scoreKeeper;
	}

	@Override
	public void afterFire(FireEvent evt) {
		if (Enterprise.is(evt.target)) {
			Enterprise enterprise = evt.target.as();
			if (enterprise.getShields().getValue() <= 0)
				gameOver(GameOverEvent.Outcome.lost, "shields");

		} else if (Star.is(evt.target)) {
			Star star = evt.target.as();
			application.message(evt.weapon + " hit " + star.getName() + " at " + star.getLocation());
		}
		if (evt.actor == application.starMap.enterprise && !evt.wasAutoFire)
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
	public void klingonDestroyed(Klingon klingon) {

		getScoreKeeper().addScore(klingon.shipClass == ShipClass.Raider ? ScoreKeeper.POINTS_KLINGON_RAIDER_DESTROYED
				: ScoreKeeper.POINTS_KLINGON_BOF_DESTROYED);
		if (!application.starMap.hasKlingons())
			events.fireEvent(new GameOverEvent(GameOverEvent.Outcome.won, "All Klingons were destroyed"));
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
			application.reload();
	}

	public void startTurn() {
		getScoreKeeper().addScore(ScoreKeeper.POINTS_DAY);
		application.starMap.advanceStarDate(1);
		events.fireEvent(new TurnStartedEvent());
		events.fireEvent(new AfterTurnStartedEvent());
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
		events.fireEvent(new GameStartedEvent());
	}

	public void endTurn() {
		events.fireEvent(new TurnEndedEvent());
		events.fireEvent(new KlingonTurnStartedEvent());
		events.fireEvent(new KlingonTurnEndedEvent());
		// release resources so that it can be (hopefully) garbage collected; at this
		// point, everyone who needs resources should have them
	}

	@Override
	public void onTurnYielded(YieldTurnEvent evt) {
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

	public void gameOver(Outcome outcome, String reason) {
		events.fireEvent(new GameOverEvent(outcome, reason));
	}

	@Override
	public void handleEnergyConsumption(Thing consumer, double value, String type) {
		if (Enterprise.is(consumer)) {
			Enterprise enterprise = consumer.as();
			if (enterprise.getAntimatter().getValue() <= 0) {
				application.message("We run out of anti matter");
				gameOver(Outcome.lost, "We run out of anti matter");
			}
		}
	}

	@Override
	public void onEnterpriseDocked(Enterprise enterprise, StarBase starBase) {
		getScoreKeeper().addScore(ScoreKeeper.POINTS_DOCK_STARBASE);
	}

}
