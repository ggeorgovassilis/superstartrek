package superstartrek.client.activities.messages;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import superstartrek.client.activities.PopupView;

public class MessagesView extends PopupView<MessagesPresenter> {

	Element eContent;
	Element eButton;

	@Override
	public void decorateWidget() {
		eContent = getHtmlPanel().getElementById("messages-content");
		eButton = getHtmlPanel().getElementById("dismiss-message-button");
		getWidget().getElement().setAttribute("id", "messages");
		DOM.sinkEvents(eButton, Event.ONCLICK | Event.ONKEYDOWN | Event.ONKEYPRESS);
		DOM.setEventListener(eButton, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				getPresenter().dismissButtonClicked();
			}
		});
		super.decorateWidget();
	}

	public MessagesView(MessagesPresenter presenter) {
		super(presenter);
	}

	public void clear() {
		eContent.setInnerHTML("");
	}

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
		eButton.focus();
	}


	@Override
	protected String getContentForHtmlPanel() {
		return getPresenter().getApplication().getResources().messages().getText();
	}
}
