package superstartrek.client.activities.loading;

import com.google.gwt.event.shared.EventHandler;

public interface GameOverHandler extends EventHandler{

	void gameOver();
	void gameLost();
	void gameWon();
}
