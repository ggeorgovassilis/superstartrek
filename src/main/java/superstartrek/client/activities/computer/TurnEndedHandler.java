package superstartrek.client.activities.computer;

import com.google.gwt.event.shared.EventHandler;

public interface TurnEndedHandler extends EventHandler{
	
	void onTurnEnded(TurnEndedEvent evt);
}
