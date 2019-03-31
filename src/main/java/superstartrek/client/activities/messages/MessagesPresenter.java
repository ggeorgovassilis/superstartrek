package superstartrek.client.activities.messages;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.utils.Timer;

public class MessagesPresenter extends BasePresenter<MessageActivity> implements MessageHandler {

	public MessagesPresenter(Application application) {
		super(application);
		application.events.addHandler(MessageEvent.TYPE, this);
	}

	@Override
	public void messagePosted(String formattedMessage, String category) {
		((MessagesView) getView()).showMessage(formattedMessage, category);
		if (!getView().isVisible())
			getView().show();
	}

	public void dismissButtonClicked() {
		hideMessages();
	}
	
	public void hideMessages() {
		if (!getView().isVisible())
			return;
		hideMessagesNow();
	}
	
	public void hideMessagesNow() {
		getView().hide();
		((MessagesView) getView()).clear();
		application.events.fireEvent(new MessageEvent(MessageEvent.Action.hide, null, null));
	}

	@Override
	public void messagesAcknowledged() {
	}

}
