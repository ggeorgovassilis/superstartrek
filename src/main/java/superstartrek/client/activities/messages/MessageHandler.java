package superstartrek.client.activities.messages;

import superstartrek.client.bus.EventHandler;

public interface MessageHandler extends EventHandler{

	default void messagePosted(String formattedMessage, String category) {};
	default void messagesAcknowledged() {};
}
