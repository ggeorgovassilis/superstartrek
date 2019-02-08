package superstartrek.client.activities.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.HTMLPanel;

import superstartrek.client.Application;
import superstartrek.client.activities.BaseView;
import superstartrek.client.activities.Presenter;
import superstartrek.client.activities.glasspanel.GlassPanelPresenter;

public class MessagesView extends BaseView<MessageActivity>{
	
	protected Element eContent;
	protected Element eButton;

	@Override
	public void finishUiConstruction() {
		super.finishUiConstruction();
		eContent = DOM.getElementById("messages-content");
		eButton = DOM.getElementById("dismiss-message-button");
		DOM.sinkEvents(eButton, Event.ONCLICK | Event.ONKEYDOWN | Event.ONKEYPRESS);
		DOM.setEventListener(eButton, new EventListener() {
			
			@Override
			public void onBrowserEvent(Event event) {
				((MessagesPresenter)getPresenter()).dismissButtonClicked();
			}
		});
		hide();
	}
	
	public MessagesView(Presenter<MessageActivity> presenter) {
		super(presenter);
	}
	
	@Override
	protected HTMLPanel createPanel() {
		return HTMLPanel.wrap(DOM.getElementById("messages"));
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
		super.show();
		eButton.focus();
	}
}
