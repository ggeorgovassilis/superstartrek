package superstartrek.client.activities.loading;

import com.google.gwt.event.shared.EventHandler;

public interface GameStartedHandler extends EventHandler{
	
	void onGameStared(GameStartedEvent evt);
}
