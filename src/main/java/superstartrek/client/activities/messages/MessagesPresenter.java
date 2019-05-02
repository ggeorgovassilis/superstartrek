package superstartrek.client.activities.messages;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.PopupViewPresenter;

public class MessagesPresenter extends BasePresenter<MessagesView>
		implements MessageHandler, PopupViewPresenter<MessagesView> {

	public MessagesPresenter(Application application) {
		super(application);
		addHandler(MessagePostedEvent.TYPE, this);
	}

	@Override
	public void messagePosted(String formattedMessage, String category) {
		view.showMessage(formattedMessage, category);
		// the "if" check sometimes says that msg is visible while it isn't; probably
		// because it's a popup. disabling until further notice
		// if (!view.isVisible())
		view.show();
	}

	public void hideMessages() {
		if (!view.isVisible())
			return;
		view.hide(() -> {
			view.clear();
			application.events.fireEvent(new MessagesReadEvent());
		});
	}

	@Override
	public void userWantsToDismissPopup() {
		hideMessages();
	}

}
