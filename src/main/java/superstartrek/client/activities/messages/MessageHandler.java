package superstartrek.client.activities.messages;

import superstartrek.client.bus.BaseHandler;

public interface MessageHandler extends BaseHandler{

	default void messagePosted(String formattedMessage, String category) {};
	default void messagesAcknowledged() {};
}
