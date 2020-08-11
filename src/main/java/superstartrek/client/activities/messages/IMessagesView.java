package superstartrek.client.activities.messages;

import superstartrek.client.activities.IPopupView;

public interface IMessagesView extends IPopupView<MessagesPresenter>{

	void clear();

	void showMessage(String formattedMessage, String category);

}