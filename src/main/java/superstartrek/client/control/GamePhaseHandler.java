package superstartrek.client.control;

import superstartrek.client.eventbus.EventHandler;
import superstartrek.client.space.StarMap;

public interface GamePhaseHandler extends EventHandler {
	
	default void onPlayerTurnStarted() {};
	
	default void onTurnStarted() {};

	default void onTurnEnded() {};

	default void onTurnYielded() {};

	default void onKlingonTurnStarted() {};

	default void gameOver() {};
	
	default void gameLost() {};
	
	default void gameWon() {};
	
	default void onGameStarted(StarMap map) {};
	
	default void beforeGameRestart() {};
	
}
