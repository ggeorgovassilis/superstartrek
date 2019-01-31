package superstartrek.client.activities.messages;

import com.google.gwt.event.shared.EventHandler;

public interface MessageHandler extends EventHandler{

	void showMessage(String formattedMessage, String category);
}
