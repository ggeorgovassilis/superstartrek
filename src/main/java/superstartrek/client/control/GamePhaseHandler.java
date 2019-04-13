package superstartrek.client.control;

import com.google.gwt.event.shared.EventHandler;

public interface GamePhaseHandler extends EventHandler {
	
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
	
}
