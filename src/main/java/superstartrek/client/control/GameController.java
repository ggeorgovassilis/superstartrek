package superstartrek.client.control;

import superstartrek.client.Application;
import superstartrek.client.activities.combat.CombatHandler;
import superstartrek.client.activities.computer.EnergyConsumptionHandler;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.messages.MessageHandler;
import superstartrek.client.activities.navigation.NavigationHandler;
import superstartrek.client.bus.EventBus;
import static superstartrek.client.bus.Events.*;

import com.google.gwt.core.client.GWT;

import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Star;
import superstartrek.client.model.StarBase;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;
import superstartrek.client.model.Weapon;
import superstartrek.client.utils.BaseMixin;
import superstartrek.client.utils.Timer;

public class GameController implements GamePhaseHandler, CombatHandler, NavigationHandler,
		MessageHandler, EnergyConsumptionHandler, BaseMixin{

	Application application;
	EventBus events;
	boolean gameIsRunning = true;
	boolean startTurnPending = false;
	boolean endTurnPending = false;
	boolean gameIsLoading = false;
	ScoreKeeper scoreKeeper;
	
	
	@Override
	public Application getApplication() {
		return application;
	}
	
	@Override
	public EventBus getEvents() {
		return events;
	}
	
	public GameController(Application application, ScoreKeeper scoreKeeper) {
		this.application = application;
		events = application.eventBus;
		this.scoreKeeper = scoreKeeper;
		addHandler(GAME_STARTED, this);
		addHandler(GAME_OVER, this);
		addHandler(TURN_STARTED, this);
		addHandler(TURN_ENDED, this);
		addHandler(KLINGON_TURN_STARTED, this);
		addHandler(AFTER_FIRE, this);
		addHandler(THING_MOVED, this);
		addHandler(KLINGON_DESTROYED, this);
		addHandler(MESSAGE_READ, this);
		addHandler(TURN_YIELDED, this);
		addHandler(CONSUME_ENERGY, this);
		addHandler(ENTERPRISE_DOCKED, this);
		addHandler(GAME_RESTART, this);
		addHandler(ENTERPRISE_DAMAGED, this);
	}
	
	@Override
	public void onGameStarted(StarMap map) {
		if (!gameIsLoading && application.gameSaver.doesSavedGameExist()) {
			gameIsLoading = true;
			boolean success = application.gameSaver.loadGame();
			application.gameSaver.deleteGame();
			if (!success)
				Timer.postpone(()->application.restart());
			gameIsLoading = false;
		}
	}

	public ScoreKeeper getScoreKeeper() {
		return scoreKeeper;
	}

	@Override
	public void afterFire(Quadrant quadrant, Vessel actor, Thing target, Weapon weapon, double damage, boolean wasAutoFire) {
		if (Enterprise.is(target)) {
			Enterprise enterprise = target.as();
			if (enterprise.getShields().getValue() <= 0)
				gameOver(GameOutcome.lost, "shields");

		} else if (Star.is(target)) {
			Star star = target.as();
			message(weapon + " hit " + star.getName() + " at " + star.getLocation());
		}
		if (actor == application.starMap.enterprise && !wasAutoFire)
			endTurnAfterThis();
	}

	@Override
	public void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
		if (thing == application.starMap.enterprise)
			endTurnAfterThis();
	}

	@Override
	public void onVesselDestroyed(Vessel vessel) {
		if (!Klingon.is(vessel))
			return;
		Klingon klingon = Klingon.as(vessel);
		getScoreKeeper().addScore(klingon.getXp());
		if (!application.starMap.hasKlingons()) {
			gameOver(GameOutcome.won, "All klingons destroyed");
		}
	}

	@Override
	public void gameOver() {
		message("Game over.");
		gameIsRunning = false;
	}

	@Override
	public void gameWon() {
		getScoreKeeper().addScore(ScoreKeeper.POINTS_GAME_WON);
		message("Congratulations, all Klingons were destroyed.", "gamewon");
		message("Your score is "+getScoreKeeper().getScore(), "score");
	}

	@Override
	public void gameLost() {
		getScoreKeeper().addScore(ScoreKeeper.POINTS_ENTERPRISE_DESTROYED);
		message("The Enterprise was destroyed.", "gameover");
		message("Your score is "+getScoreKeeper().getScore(), "score");
	}

	@Override
	public void messagesAcknowledged() {
		// TODO: this is too implicit. The intention is that, once the game has been
		// lost/won and the user clicks away the informing message, the game should
		// reload.
		if (!this.gameIsRunning)
			application.restart();
	}

	protected void startTurn() {
		getScoreKeeper().addScore(ScoreKeeper.POINTS_DAY);
		application.starMap.advanceStarDate(1);
		fireEvent(TURN_STARTED, (h)->h.onTurnStarted());
		fireEvent(AFTER_TURN_STARTED, (h)->h.afterTurnStarted());
	}

	public void startTurnAfterThis() {
		if (startTurnPending)
			return;
		startTurnPending = true;
		superstartrek.client.utils.Timer.postpone(() -> {
			startTurn();
			startTurnPending = false;
		});
	}

	public void startGame() {
		application.browserAPI.postHistoryChange("intro", true);
		fireEvent(GAME_STARTED, (h)->h.onGameStarted(application.starMap));
	}

	protected void endTurn() {
		fireEvent(TURN_ENDED, (h)->h.onTurnEnded());
		fireEvent(KLINGON_TURN_STARTED, (h)->h.onKlingonTurnStarted());
		fireEvent(KLINGON_TURN_ENDED, (h)->h.onKlingonTurnEnded());
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
			endTurn();
			startTurnAfterThis();
			endTurnPending = false;
		});
	}

	public void gameOver(GameOutcome outcome, String reason) {
		gameIsRunning=false;
		if (outcome == GameOutcome.won)
			fireEvent(GAME_OVER, (h)->h.gameWon());
		if (outcome == GameOutcome.lost)
			fireEvent(GAME_OVER, (h)->h.gameLost());
	}

	@Override
	public void handleEnergyConsumption(Thing consumer, double value, String type) {
		if (Enterprise.is(consumer)) {
			Enterprise enterprise = consumer.as();
			if (enterprise.getAntimatter().getValue() <= 0) {
				message("We run out of anti matter");
				gameOver(GameOutcome.lost, "We run out of anti matter.");
			}
		}
	}
	
	@Override
	public void onEnterpriseDamaged(Enterprise enterprise) {
		if (enterprise.getShields().getValue()<=0)
			gameOver(GameOutcome.lost, "The Enterprise was destroyed.");
	}

	@Override
	public void onEnterpriseDocked(Enterprise enterprise, StarBase starBase, int items, int torpedos, int antimatter) {
		getScoreKeeper().addScore(ScoreKeeper.POINTS_ENTERPRISE_REPAIR*(items+torpedos+(antimatter/100)));
	}
	
	@Override
	public void beforeGameRestart() {
		scoreKeeper.reset();
		gameIsRunning = true;
	}
	
	@Override
	public void onTurnEnded() {
		//TODO: ugly hack. For technical reasons, Enterprise always warps from nowhere into the
		//starting quadrant which ends the turn. This would save the game on the first turn, overwriting
		//an older saved game.
		if (application.starMap.getStarDate() > 2100)
			application.gameSaver.saveGame();
	}

}
