package superstartrek.client.activities.messages;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTMLPanel;

import superstartrek.client.activities.BaseView;
import superstartrek.client.activities.Presenter;

public class MessagesView extends BaseView<MessageActivity>{
	
	protected Element eContent;

	@Override
	public void finishUiConstruction() {
		super.finishUiConstruction();
		eContent = DOM.getElementById("messages-content");
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
}
