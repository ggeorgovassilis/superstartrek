package superstartrek.client.control;

import com.google.gwt.event.shared.EventHandler;

import superstartrek.client.bus.BaseHandler;

public interface GamePhaseHandler extends EventHandler, BaseHandler {
	
	default void onTurnStarted(TurnStartedEvent evt) {};

	default void afterTurnStarted(AfterTurnStartedEvent evt) {};

	default void onTurnEnded(TurnEndedEvent evt) {};

	default void onTurnYielded(YieldTurnEvent evt) {};

	default void onKlingonTurnStarted() {};

	default void onKlingonTurnEnded() {};

	default void gameOver() {};
	
	default void gameLost() {};
	
	default void gameWon() {};
	
	default void onGameStarted(GameStartedEvent evt) {};
	
	default void beforeGameRestart() {};
	
}
