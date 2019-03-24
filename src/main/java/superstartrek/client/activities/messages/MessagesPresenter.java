package superstartrek.client.activities.messages;

import javax.swing.text.Utilities;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.glasspanel.GlassPanelEvent;
import superstartrek.client.activities.glasspanel.GlassPanelEvent.Action;
import superstartrek.client.activities.glasspanel.GlassPanelHandler;
import superstartrek.client.utils.Timer;

public class MessagesPresenter extends BasePresenter<MessageActivity> implements MessageHandler, GlassPanelHandler {

	public MessagesPresenter(Application application) {
		super(application);
		application.events.addHandler(MessageEvent.TYPE, this);
		application.events.addHandler(GlassPanelEvent.TYPE, this);
	}

	@Override
	public void messagePosted(String formattedMessage, String category) {
		GWT.log("message posted "+formattedMessage);
		((MessagesView) getView()).showMessage(formattedMessage, category);
		application.events.fireEvent(new GlassPanelEvent(Action.show));
		getView().show();
	}

	public void dismissButtonClicked() {
		hideMessages();
	}
	
	public void hideMessages() {
		GWT.log("Hide messages async "+getView().isVisible());
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
		GWT.log("Hide messages now "+getView().isVisible());
		getView().hide();
		((MessagesView) getView()).clear();
		application.events.fireEvent(new MessageEvent(MessageEvent.Action.hide, null, null));
		application.events.fireEvent(new GlassPanelEvent(Action.hide));
	}

	@Override
	public void glassPanelShown() {
	}

	@Override
	public void glassPanelHidden() {
		GWT.log("glass panel hidden");
		hideMessages();
	}

	@Override
	public void glassPanelClicked() {
		GWT.log("glass panel clicked");
		hideMessages();
	}

	@Override
	public void messagesAcknowledged() {
	}

}
