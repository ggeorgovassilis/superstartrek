package superstartrek.client.activities.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import superstartrek.client.activities.PopupView;
import superstartrek.client.activities.Presenter;

public class MessagesView extends PopupView<MessageActivity> {

	Element eContent;
	Element eButton;

	@Override
	public void finishUiConstruction() {
		super.finishUiConstruction();
		eContent = getHtmlPanel().getElementById("messages-content");
		eButton = getHtmlPanel().getElementById("dismiss-message-button");
		getWidget().getElement().setAttribute("id", "messages");
		DOM.sinkEvents(eButton, Event.ONCLICK | Event.ONKEYDOWN | Event.ONKEYPRESS);
		DOM.setEventListener(eButton, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				((MessagesPresenter) getPresenter()).dismissButtonClicked();
			}
		});
		hide();
	}

	public MessagesView(Presenter<MessageActivity> presenter) {
		super(presenter);
	}

	public void clear() {
		eContent.setInnerHTML("");
	}

	public void showMessage(String formattedMessage, String category) {
		Element line = DOM.createElement("li");
		line.setInnerHTML(formattedMessage);
		line.addClassName("entry " + category);
		eContent.appendChild(line);
	}

	@Override
	public void show() {
		if (isVisible())
			return;
		// profiling showed high CPU usage of focus; this check attempts to reduce
		// invocations of focus
		// if (popup.isVisible())
		// return;
		super.show();
		eButton.focus();
	}


	@Override
	protected String getContentForHtmlPanel() {
		return getPresenter().getApplication().getResources().messages().getText();
	}
}
