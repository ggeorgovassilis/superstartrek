package superstartrek.client.activities.messages;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

import superstartrek.client.activities.BaseView;
import superstartrek.client.activities.Presenter;

public class MessagesView extends BaseView<MessageActivity>{
	
	Element eContent;
	Element eButton;
	PopupPanel popup;
	HTMLPanel html;
	
	@Override
	protected Widget createWidgetImplementation() {
		popup = new PopupPanel(true,true);
		popup.setGlassEnabled(true);
		popup.setGlassStyleName("glasspanel");
		html = new HTMLPanel(getPresenter().getApplication().getResources().messages().getText());
		html.getElement().setAttribute("id", "messages");
		popup.add(html);
		return new FlowPanel();
	}
	
	@Override
	public void finishUiConstruction() {
		super.finishUiConstruction();
		eContent = html.getElementById("messages-content");
		eButton = html.getElementById("dismiss-message-button");
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
	
	public void clear() {
		eContent.setInnerHTML("");
	}

	public void showMessage(String formattedMessage, String category) {
		Element line = DOM.createElement("li");
		line.setInnerHTML(formattedMessage);
		line.addClassName("entry "+category);
		eContent.appendChild(line);
	}
	
	@Override
	public void show() {
		//profiling showed high CPU usage of focus; this check attempts to reduce invocations of focus
		//if (popup.isVisible())
		//	return;
		popup.show();
		eButton.focus();
	}
	
	@Override
	public void hide() {
		popup.hide();
	}
}
