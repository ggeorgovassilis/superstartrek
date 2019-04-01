package superstartrek.client.control;

import com.google.gwt.event.shared.EventHandler;

public interface GamePhaseHandler extends EventHandler {

	default void onTurnStarted(TurnStartedEvent evt) {};

	default void onTurnEnded(TurnEndedEvent evt) {};

	default void executeKlingonMove() {};

	default void gameOver() {};
	
	default void gameLost() {};
	
	default void gameWon() {};
	
	default void onGameStarted(GameStartedEvent evt) {};

}
