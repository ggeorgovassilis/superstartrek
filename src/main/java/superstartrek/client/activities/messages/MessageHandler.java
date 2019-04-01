package superstartrek.client.activities.messages;

import com.google.gwt.event.shared.EventHandler;

public interface MessageHandler extends EventHandler{

	default void messagePosted(String formattedMessage, String category) {};
	default void messagesAcknowledged() {};
}
