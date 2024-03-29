package superstartrek.client.activities.messages;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

import superstartrek.client.activities.popup.PopupViewImpl;
import superstartrek.client.screentemplates.ScreenTemplates;

public class MessagesViewImpl extends PopupViewImpl<MessagesPresenter> implements MessagesView {

	Element eContent;
	Element eButton;
	boolean inTransition = false;

	@Override
	protected void decorateWidget(ScreenTemplates templates, Element element) {
		super.decorateWidget(templates, element);
		eContent = getElementById("messages-content");
		eButton = getElementById("dismiss-message-button");
		element.setId("messages");
		DOM.sinkEvents(eButton, Event.ONCLICK | Event.ONKEYDOWN | Event.ONKEYPRESS);
		DOM.setEventListener(eButton, (Event event) -> presenter.cancelButtonClicked());
	}

	public MessagesViewImpl(MessagesPresenter presenter) {
		super(presenter);
	}

	@Override
	public void clear() {
		eContent.setInnerHTML("");
	}

	@Override
	public void showMessage(String formattedMessage, String category) {
		Element line = Document.get().createLIElement();
		line.setInnerHTML(formattedMessage);
		line.setClassName(category);
		eContent.appendChild(line);
	}

	@Override
	public void show() {
		if (inTransition || isVisible())
			return;
		inTransition = true;
		//Most (all?) messages arrive within the same slot between user interactions on the main js thread.
		//Modifying the view with messages during the popup animation would slow it down considerably.
		//Deferring the apparence ensures all DOM modifications have finished before the animation.
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
	protected String getContentForHtmlPanel(ScreenTemplates templates) {
		return templates.messages().getText();
	}
}
