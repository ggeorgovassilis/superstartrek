package superstartrek.client.control;

import com.google.gwt.event.shared.EventHandler;

import superstartrek.client.bus.BaseHandler;
import superstartrek.client.model.StarMap;

public interface GamePhaseHandler extends EventHandler, BaseHandler {
	
	default void onTurnStarted(TurnStartedEvent evt) {};

	default void afterTurnStarted() {};

	default void onTurnEnded(TurnEndedEvent evt) {};

	default void onTurnYielded() {};

	default void onKlingonTurnStarted() {};

	default void onKlingonTurnEnded() {};

	default void gameOver() {};
	
	default void gameLost() {};
	
	default void gameWon() {};
	
	default void onGameStarted(StarMap map) {};
	
	default void beforeGameRestart() {};
	
}
