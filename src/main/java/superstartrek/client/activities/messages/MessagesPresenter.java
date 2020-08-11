package superstartrek.client.activities.messages;

import com.google.gwt.event.dom.client.KeyCodes;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.PopupViewPresenter;
import superstartrek.client.bus.Events;
import superstartrek.client.control.KeyPressedEventHandler;

public class MessagesPresenter extends BasePresenter<IMessagesView>
		implements MessageHandler, PopupViewPresenter<IMessagesView>, KeyPressedEventHandler{

	public MessagesPresenter(Application application) {
		super(application);
		addHandler(Events.MESSAGE_POSTED, this);
	}

	@Override
	public void messagePosted(String formattedMessage, String category) {
		if (!view.isVisible())
			addHandler(Events.KEY_PRESSED, this);
		view.showMessage(formattedMessage, category);
		// the "if" check sometimes says that msg is visible while it isn't; probably
		// because it's a popup. disabling until further notice
		// if (!view.isVisible())
		view.show();
	}

	public void hideMessages() {
		if (!view.isVisible())
			return;
		removeHandler(Events.KEY_PRESSED, this);
		view.hide(() -> {
			view.clear();
			fireEvent(Events.MESSAGE_READ, (h)->h.messagesAcknowledged());
		});
	}

	@Override
	public void userWantsToDismissPopup() {
		hideMessages();
	}

	@Override
	public void onKeyPressed(int keyCode) {
		switch (keyCode){
			case KeyCodes.KEY_ESCAPE:
			case KeyCodes.KEY_ENTER:
				hideMessages();
		}
	}

}
