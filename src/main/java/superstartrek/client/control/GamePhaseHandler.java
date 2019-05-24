package superstartrek.client.control;

import superstartrek.client.bus.EventHandler;
import superstartrek.client.model.StarMap;

public interface GamePhaseHandler extends EventHandler {
	
	default void onTurnStarted() {};

	default void afterTurnStarted() {};

	default void onTurnEnded() {};

	default void onTurnYielded() {};

	default void onKlingonTurnStarted() {};

	default void onKlingonTurnEnded() {};

	default void gameOver() {};
	
	default void gameLost() {};
	
	default void gameWon() {};
	
	default void onGameStarted(StarMap map) {};
	
	default void beforeGameRestart() {};
	
}
