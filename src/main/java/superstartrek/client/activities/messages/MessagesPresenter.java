package superstartrek.client.activities.messages;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;

public class MessagesPresenter extends BasePresenter<MessagesView> implements MessageHandler {

	public MessagesPresenter(Application application) {
		super(application);
		addHandler(MessagePostedEvent.TYPE, this);
	}

	@Override
	public void messagePosted(String formattedMessage, String category) {
		view.showMessage(formattedMessage, category);
		// the "if" check sometimes says that msg is visible while it isn't; probably because it's a popup. disabling until further notice
		//if (!view.isVisible())
		view.show();
	}

	public void dismissButtonClicked() {
		hideMessages();
	}
	
	public void hideMessages() {
		if (!view.isVisible())
			return;
		view.hide(new ScheduledCommand() {
			@Override
			public void execute() {
				view.clear();
				application.events.fireEvent(new MessagesReadEvent());
			}
		});
	}
	

}
