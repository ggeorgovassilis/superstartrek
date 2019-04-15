package superstartrek.client.control;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.History;
import superstartrek.client.Application;
import superstartrek.client.activities.combat.FireHandler;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.KlingonDestroyedHandler;
import superstartrek.client.activities.messages.MessageHandler;
import superstartrek.client.activities.navigation.EnterpriseRepairedEvent;
import superstartrek.client.activities.navigation.EnterpriseRepairedHandler;
import superstartrek.client.activities.navigation.ThingMovedEvent;
import superstartrek.client.activities.navigation.ThingMovedHandler;
import superstartrek.client.control.GameOverEvent.Outcome;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Star;
import superstartrek.client.model.Thing;

public class GameController implements GamePhaseHandler, FireHandler, EnterpriseRepairedHandler, ThingMovedHandler, KlingonDestroyedHandler, MessageHandler{

	Application application;
	public boolean gameIsRunning = true;
	protected boolean startTurnPending = false;
	protected boolean endTurnPending = false;

	
	public GameController(Application application) {
		this.application = application;
		application.events.addHandler(GameStartedEvent.TYPE, this);
		application.events.addHandler(GameOverEvent.TYPE, this);
		application.events.addHandler(TurnStartedEvent.TYPE, this);
		application.events.addHandler(TurnEndedEvent.TYPE, this);
		application.events.addHandler(KlingonTurnStartedEvent.TYPE, this);
		application.events.addHandler(FireEvent.TYPE, this);
		application.events.addHandler(EnterpriseRepairedEvent.TYPE, this);
		application.events.addHandler(ThingMovedEvent.TYPE, this);
		application.events.addHandler(KlingonDestroyedEvent.TYPE, this);
		application.events.addHandler(MessagesReadEvent.TYPE, this);
		application.events.addHandler(YieldTurnEvent.TYPE, this);
	}

	@Override
	public void afterFire(FireEvent evt) {
		
		if (evt.target instanceof Star) {
			Star star = (Star)evt.target;
			Application.get().message(evt.weapon + " hit " + star.getName() + " at " + star.getLocation());
		}
		
		if (evt.target instanceof Enterprise) {
			Enterprise enterprise = (Enterprise)evt.target;
			if (enterprise.getShields().getValue()<=0)
				gameOver(GameOverEvent.Outcome.lost, "shields");

		} else
		if (evt.actor == application.starMap.enterprise && !evt.wasAutoFire)
			endTurnAfterThis();
	}

	@Override
	public void onEnterpriseRepaired(Enterprise enterprise) {
		endTurnAfterThis();
	}

	@Override
	public void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
		if (thing == application.starMap.enterprise)
			endTurnAfterThis();
	}

	@Override
	public void klingonDestroyed(Klingon klingon) {
		if (!application.starMap.hasKlingons())
			application.events.fireEvent(new GameOverEvent(GameOverEvent.Outcome.won, "All Klingons were destroyed"));
	}
	
	@Override
	public void gameOver() {
		application.message("Game over.");
		gameIsRunning = false;
	}
	
	@Override
	public void gameWon() {
		application.message("Congratulations, all Klingons were destroyed.", "gamewon");
	}
	
	@Override
	public void gameLost() {
		application.message("The Enterprise was destroyed.", "gameover");
	}
	
	@Override
	public void messagesAcknowledged() {
		//TODO: this is too implicit. The intention is that, once the game has been lost/won and the user clicks away the informing message, the game should reload.
		if (!this.gameIsRunning)
			application.reload();
	}
	
	
	public void startTurn() {
		GWT.log("------------------------------ new turn");
		application.starMap.advanceStarDate(1);
		application.events.fireEvent(new TurnStartedEvent());
		application.events.fireEvent(new AfterTurnStartedEvent());
	}
	
	public void startTurnAfterThis() {
		if (startTurnPending)
			return;
		startTurnPending = true;
		superstartrek.client.utils.Timer.postpone(new Scheduler.ScheduledCommand() {
			
			@Override
			public void execute() {
				startTurnPending = false;
				startTurn();
			}
		});
	}
	
	public void startGame() {
		History.newItem("intro", true);
		application.registerEventHandlers();
		application.events.fireEvent(new GameStartedEvent());
	}

	public void endTurn() {
		application.events.fireEvent(new TurnEndedEvent());
		application.events.fireEvent(new KlingonTurnStartedEvent());
		application.events.fireEvent(new KlingonTurnEndedEvent());
		//release resources so that it can be (hopefully) garbage collected; at this point, everyone who needs resources should have them
	}
	
	@Override
	public void onTurnYielded(YieldTurnEvent evt) {
		endTurnAfterThis();
	}

	
	public void endTurnAfterThis() {
		if (endTurnPending)
			return;
		endTurnPending = true;
		superstartrek.client.utils.Timer.postpone(new Scheduler.ScheduledCommand() {
			
			@Override
			public void execute() {
				endTurnPending = false;
				endTurn();
				startTurnAfterThis();
			}
		});
	}

	public void gameOver(Outcome outcome, String reason) {
		application.events.fireEvent(new GameOverEvent(outcome, reason));
	}

}
