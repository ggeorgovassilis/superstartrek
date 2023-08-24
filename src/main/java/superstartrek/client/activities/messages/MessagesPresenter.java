package superstartrek.client.activities.messages;

import com.google.gwt.event.dom.client.KeyCodes;

import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.PopupViewPresenter;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.KeyPressedEventHandler;
import superstartrek.client.eventbus.Events;
import superstartrek.client.space.StarMap;

public class MessagesPresenter extends BasePresenter<IMessagesView>
		implements MessageHandler, PopupViewPresenter<IMessagesView>, KeyPressedEventHandler, GamePhaseHandler{

	public MessagesPresenter() {
		addHandler(Events.GAME_STARTED);
		addHandler(Events.GAME_OVER);
		addHandler(Events.MESSAGE_POSTED);
	}

	@Override
	public void messagePosted(String formattedMessage, String category) {
		if (!view.isVisible())
			addHandler(Events.KEY_PRESSED);
		view.showMessage(formattedMessage, category);
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
	public void cancelButtonClicked() {
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
	
	@Override
	public void onGameStarted(StarMap map) {
		removeHandler(Events.MESSAGE_POSTED, this);
		addHandler(Events.MESSAGE_POSTED);
	}
	
	@Override
	public void gameOver() {
		removeHandler(Events.MESSAGE_POSTED, this);
	}

}
