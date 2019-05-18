package superstartrek.client.activities.messages;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.PopupViewPresenter;
import superstartrek.client.bus.Events;

public class MessagesPresenter extends BasePresenter<MessagesView>
		implements MessageHandler, PopupViewPresenter<MessagesView> {

	public MessagesPresenter(Application application) {
		super(application);
		addHandler(Events.MESSAGE_POSTED, this);
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
			fireEvent(Events.MESSAGE_READ, (h)->h.messagesAcknowledged());
		});
	}

	@Override
	public void userWantsToDismissPopup() {
		hideMessages();
	}

}
