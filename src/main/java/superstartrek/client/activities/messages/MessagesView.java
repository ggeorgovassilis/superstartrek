package superstartrek.client.activities.messages;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import superstartrek.client.activities.PopupView;

public class MessagesView extends PopupView<MessagesPresenter> implements IMessagesView {

	Element eContent;
	Element eButton;
	boolean inTransition = false;

	@Override
	public void decorateWidget() {
		super.decorateWidget();
		eContent = getElementById("messages-content");
		eButton = getElementById("dismiss-message-button");
		getElement().setId("messages");
		DOM.sinkEvents(eButton, Event.ONCLICK | Event.ONKEYDOWN | Event.ONKEYPRESS);
		DOM.setEventListener(eButton, (Event event) -> presenter.userWantsToDismissPopup());
	}

	public MessagesView(MessagesPresenter presenter) {
		super(presenter);
	}

	@Override
	public void clear() {
		eContent.setInnerHTML("");
	}

	@Override
	public void showMessage(String formattedMessage, String category) {
		Element line = DOM.createElement("li");
		line.setInnerHTML(formattedMessage);
		line.setClassName(category);
		eContent.appendChild(line);
	}

	@Override
	public void show() {
		if (inTransition || isVisible())
			return;
		inTransition = true;
		superstartrek.client.utils.Timer.postpone(()->{
			super.show();
			inTransition = false;
		});
	}
	
	@Override
	protected void animationIsDone() {
		eButton.focus();
	}

	@Override
	protected String getContentForHtmlPanel() {
		return presenter.getApplication().getScreenTemplates().messages().getText();
	}
}
