package superstartrek.client.activities.messages;

import com.google.gwt.core.client.GWT;
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
		GWT.log("messagePosted "+formattedMessage);
		((MessagesView) getView()).showMessage(formattedMessage, category);
		// the "if" check sometimes says that msg is visible while it isn't; probably because it's a popup. disabling until further notice
		//		if (!getView().isVisible())
		getView().show();
	}

	public void dismissButtonClicked() {
		hideMessages();
	}
	
	public void hideMessages() {
		if (!getView().isVisible())
			return;
		Timer.postpone(new ScheduledCommand() {
			@Override
			public void execute() {
				hideMessagesNow();
			}
		});
	}
	
	public void hideMessagesNow() {
		getView().hide();
		((MessagesView) getView()).clear();
		application.events.fireEvent(new MessageEvent(MessageEvent.Action.hide, null, null));
	}

}
