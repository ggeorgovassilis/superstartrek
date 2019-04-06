package superstartrek.client.activities.messages;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;

public class MessagesPresenter extends BasePresenter<MessageActivity> implements MessageHandler {

	public MessagesPresenter(Application application) {
		super(application);
		application.events.addHandler(MessagePostedEvent.TYPE, this);
	}

	@Override
	public void messagePosted(String formattedMessage, String category) {
		((MessagesView) getView()).showMessage(formattedMessage, category);
		// the "if" check sometimes says that msg is visible while it isn't; probably because it's a popup. disabling until further notice
		if (!getView().isVisible())
			getView().show();
	}

	public void dismissButtonClicked() {
		hideMessages();
	}
	
	public void hideMessages() {
		if (!getView().isVisible())
			return;
		getView().hide(new ScheduledCommand() {
			@Override
			public void execute() {
				((MessagesView) getView()).clear();
				application.events.fireEvent(new MessagesReadEvent());
			}
		});
	}
	

}
