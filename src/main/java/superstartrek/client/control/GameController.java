package superstartrek.client.control;

import static superstartrek.client.eventbus.Events.*;

import java.util.Date;

import superstartrek.client.Application;
import superstartrek.client.activities.computer.EnergyConsumptionHandler;
import superstartrek.client.activities.messages.MessageHandler;
import superstartrek.client.activities.messages.MessagesMixin;
import superstartrek.client.activities.navigation.NavigationHandler;
import superstartrek.client.eventbus.EventBus;
import superstartrek.client.eventbus.EventsMixin;
import superstartrek.client.space.Constants;
import superstartrek.client.space.Location;
import superstartrek.client.space.Quadrant;
import superstartrek.client.space.Star;
import superstartrek.client.space.StarBase;
import superstartrek.client.space.StarMap;
import superstartrek.client.space.Thing;
import superstartrek.client.utils.BaseMixin;
import superstartrek.client.utils.Timer;
import superstartrek.client.vessels.CombatHandler;
import superstartrek.client.vessels.Enterprise;
import superstartrek.client.vessels.Klingon;
import superstartrek.client.vessels.Vessel;
import superstartrek.client.vessels.Weapon;

public class GameController implements GamePhaseHandler, CombatHandler, NavigationHandler,
		MessageHandler, EnergyConsumptionHandler, BaseMixin, QuadrantActivationHandler, MessagesMixin, EventsMixin{

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
		addHandler(QUADRANT_ACTIVATED, this);
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
		if (actor == getEnterprise() && !wasAutoFire)
			endTurnAfterThis();
	}

	@Override
	public void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
		if (thing == getEnterprise())
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
		scoreKeeper.addScore(ScoreKeeper.POINTS_GAME_WON);
		message("Congratulations, all Klingons were destroyed.", "gamewon");
		message("Your score is "+getScoreKeeper().getScore(), "score");
	}

	@Override
	public void gameLost() {
		scoreKeeper.addScore(ScoreKeeper.POINTS_ENTERPRISE_DESTROYED);
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
		scoreKeeper.addScore(ScoreKeeper.POINTS_DAY);
		application.starMap.advanceStarDate(1);
		fireEvent(TURN_STARTED, (h)->h.onTurnStarted());
		fireEvent(PLAYER_TURN_STARTED, (h)->h.onPlayerTurnStarted());
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
		message("Game "+outcome.toString(), "gameover");
		if (outcome == GameOutcome.won)
			fireEvent(GAME_WON, (h)->h.gameWon());
		if (outcome == GameOutcome.lost)
			fireEvent(GAME_LOST, (h)->h.gameLost());
		scoreKeeper.commitScore(new Date());
		fireEvent(GAME_OVER, (h)->h.gameOver());
	}

	@Override
	public void handleEnergyConsumption(Thing consumer, double value, String type) {
		if (Enterprise.is(consumer)) {
			Enterprise enterprise = consumer.as();
			if (enterprise.getAntimatter().getValue() <= 0) {
				message("We ran out of anti matter");
				gameOver(GameOutcome.lost, "We ran out of anti matter.");
			}
		}
	}
	
	@Override
	public void onEnterpriseDamaged(Enterprise enterprise) {
		if (enterprise.getShields().getValue()<=0) {
			gameOver(GameOutcome.lost, "The Enterprise was destroyed.");
		}
	}

	@Override
	public void onEnterpriseDocked(Enterprise enterprise, StarBase starBase, int items, int torpedos, int antimatter) {
		getScoreKeeper().addScore(ScoreKeeper.POINTS_ENTERPRISE_REPAIR*(items+torpedos+(antimatter/100)));
		endTurnAfterThis();
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
		//postponing in timer in order to avoid UI lag
		if (application.starMap.getStarDate() > Constants.START_DATE)
			Timer.postpone(()->application.gameSaver.saveGame());
	}

	@Override
	public void onActiveQuadrantChanged(Quadrant oldQuadrant, Quadrant newQuadrant) {
		oldQuadrant.dehydrate();
		newQuadrant.hydrate();
	}

}
