package superstartrek.client.activities.messages;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import superstartrek.client.activities.PopupView;
import superstartrek.client.utils.HtmlWidget;

public class MessagesView extends PopupView<MessagesPresenter> implements IMessagesView {

	Element eContent;
	Element eButton;

	@Override
	public void decorateWidget() {
		super.decorateWidget();
		HtmlWidget panel = getWidgetAs();
		eContent = panel.getElementById("messages-content");
		eButton = panel.getElementById("dismiss-message-button");
		panel.getElement().setId("messages");
		DOM.sinkEvents(eButton, Event.ONCLICK | Event.ONKEYDOWN | Event.ONKEYPRESS);
		DOM.setEventListener(eButton, (Event event) -> {
			presenter.userWantsToDismissPopup();
		});
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
		line.addClassName(category);
		eContent.appendChild(line);
	}

	@Override
	public void show() {
		if (isVisible())
			return;
		super.show();
	}
	
	@Override
	protected void animationIsDone() {
		//not calling super because that would be an unncecessary double focus (super.animationIsDone focusses
		//the entire popup view
		eButton.focus();
	}

	@Override
	protected String getContentForHtmlPanel() {
		return presenter.getApplication().getResources().messages().getText();
	}
}
