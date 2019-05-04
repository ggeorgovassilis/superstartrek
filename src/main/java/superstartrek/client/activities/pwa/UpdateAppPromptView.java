package superstartrek.client.activities.pwa;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import superstartrek.client.Application;
import superstartrek.client.activities.PopupView;

public class UpdateAppPromptView extends PopupView<UpdateAppPromptPresenter>{
	
	public void disableButtons() {
		Element e = DOM.getElementById("update-yes");
		e.setAttribute("disabled", "disabled");
		e.addClassName("disabled");
		e = DOM.getElementById("update-no");
		e.setAttribute("disabled", "disabled");
		e.addClassName("disabled");
	}

	public UpdateAppPromptView(UpdateAppPromptPresenter presenter) {
		super(presenter);
	}
	
	@Override
	public void decorateWidget() {
		super.decorateWidget();
		getElement().setId("update-app-prompt");
		addHandler((event) -> {
				Element target = event.getNativeEvent().getEventTarget().cast();
				String id = target.getId();
				if ("update-yes".equals(id)) {
					presenter.acceptUpdateButtonClicked();
				} else
				if ("update-no".equals(id)) {
					presenter.userWantsToDismissPopup();
				}
		}, ClickEvent.getType());
	}

	@Override
	protected String getContentForHtmlPanel() {
		return presenter.getApplication().getResources().updateAppPrompt().getText();
	}
}
