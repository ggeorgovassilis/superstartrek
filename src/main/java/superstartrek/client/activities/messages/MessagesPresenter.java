package superstartrek.client.activities.messages;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.glasspanel.GlassPanelEvent;
import superstartrek.client.activities.glasspanel.GlassPanelEvent.Action;
import superstartrek.client.activities.glasspanel.GlassPanelHandler;

public class MessagesPresenter extends BasePresenter<MessageActivity> implements MessageHandler, GlassPanelHandler{

	public MessagesPresenter(Application application) {
		super(application);
		application.events.addHandler(MessageEvent.TYPE, this);
		application.events.addHandler(GlassPanelEvent.TYPE, this);
	}

	@Override
	public void showMessage(String formattedMessage, String category) {
		((MessagesView)getView()).showMessage(formattedMessage, category);
		application.events.fireEvent(new GlassPanelEvent(Action.show));
		getView().show();
	}

	public void hideMessages() {
		((MessagesView)getView()).clear();
		getView().hide();
		application.events.fireEvent(new MessageEvent(MessageEvent.Action.hide, null, null));
	}

	@Override
	public void glassPanelShown() {
	}

	@Override
	public void glassPanelHidden() {
	}

	@Override
	public void glassPanelClicked() {
		hideMessages();
	}

	@Override
	public void messagesHidden() {
	}

}
