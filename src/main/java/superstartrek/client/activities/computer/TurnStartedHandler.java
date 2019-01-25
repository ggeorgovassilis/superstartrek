package superstartrek.client.activities.computer;

import com.google.gwt.event.shared.EventHandler;

public interface TurnStartedHandler extends EventHandler{
	
	void onTurnStarted(TurnStartedEvent evt);
}
