package superstartrek.client.activities.pwa;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;

import superstartrek.client.activities.CSS;
import superstartrek.client.activities.PopupView;

public class UpdateAppPromptView extends PopupView<UpdateAppPromptPresenter>{
	
	protected void disable(String id) {
		Element e = DOM.getElementById(id);
		CSS.setEnabled(e, false);
	}
	
	public void disableButtons() {
		disable("update-yes");
		disable("update-no");
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
