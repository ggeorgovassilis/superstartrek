package superstartrek.client.activities.messages;

import superstartrek.client.activities.PopupView;

public interface MessagesView extends PopupView<MessagesPresenter>{

	void clear();

	void showMessage(String formattedMessage, String category);

}