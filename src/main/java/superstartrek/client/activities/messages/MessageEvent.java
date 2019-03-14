package superstartrek.client.activities.messages;

import com.google.gwt.event.shared.GwtEvent;

public class MessageEvent extends GwtEvent<MessageHandler> {

	public enum Action{show,hide};
	public static Type<MessageHandler> TYPE = new Type<MessageHandler>();

	protected final String formattedMessage;
	protected final String category;
	protected final Action action;
	
	public MessageEvent(Action action, String formattedMessage, String category) {
		this.action = action;
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

	public Action getAction() {
		return action;
	}

	@Override
	protected void dispatch(MessageHandler handler) {
		if (action == MessageEvent.Action.show)
			handler.messagePosted(formattedMessage, category);
		else if (action == MessageEvent.Action.hide)
			handler.messagesAcknowledged();
	}
}
