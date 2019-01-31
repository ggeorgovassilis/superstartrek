package superstartrek.client.activities.messages;

import com.google.gwt.event.shared.GwtEvent;

public class MessageEvent extends GwtEvent<MessageHandler> {

	public static Type<MessageHandler> TYPE = new Type<MessageHandler>();

	protected String formattedMessage;
	protected String category;
	
	public MessageEvent(String formattedMessage, String category) {
		this.formattedMessage = formattedMessage;
		this.category = category;
	}

	@Override
	public Type<MessageHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MessageHandler handler) {
		handler.showMessage(formattedMessage, category);
	}

}
