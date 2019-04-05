package superstartrek.client.activities.messages;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface MessageHandler extends EventHandler{

	public static class MessagePostedEvent extends GwtEvent<MessageHandler> {

		public static Type<MessageHandler> TYPE = new Type<MessageHandler>();

		protected final String formattedMessage;
		protected final String category;
		
		public MessagePostedEvent(String formattedMessage, String category) {
			this.formattedMessage = formattedMessage;
			this.category = category;
		}

		@Override
		public Type<MessageHandler> getAssociatedType() {
			return TYPE;
		}

		public String getFormattedMessage() {
			return formattedMessage;
		}

		public String getCategory() {
			return category;
		}

		@Override
		protected void dispatch(MessageHandler handler) {
			handler.messagePosted(formattedMessage, category);
		}
	}

	public static class MessagesReadEvent extends GwtEvent<MessageHandler> {

		public static Type<MessageHandler> TYPE = new Type<MessageHandler>();

		public MessagesReadEvent() {
		}

		@Override
		public Type<MessageHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(MessageHandler handler) {
			handler.messagesAcknowledged();
		}
	}
	
	default void messagePosted(String formattedMessage, String category) {};
	default void messagesAcknowledged() {};
}
