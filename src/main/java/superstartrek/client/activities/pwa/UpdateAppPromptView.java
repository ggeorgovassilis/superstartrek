package superstartrek.client.activities.pwa;

import java.util.logging.Logger;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import superstartrek.client.Application;
import superstartrek.client.activities.PopupView;

public class UpdateAppPromptView extends PopupView<UpdateAppPromptPresenter>{
	
	private static Logger log = Logger.getLogger("");
	public void disableButtons() {
		DOM.getElementById("update-yes").setAttribute("disabled", "disabled");
		DOM.getElementById("update-no").setAttribute("disabled", "disabled");
	}

	public UpdateAppPromptView(UpdateAppPromptPresenter presenter) {
		super(presenter);
	}
	
	@Override
	public void decorateWidget() {
		super.decorateWidget();
		getElement().setId("update-app-prompt");
		addHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Element target = event.getNativeEvent().getEventTarget().cast();
				String id = target.getId();
				if ("update-yes".equals(id)) {
					presenter.acceptUpdateButtonClicked();
				}
				if ("update-no".equals(id)) {
					presenter.userWantsToDismissPopup();
					
				}
			}
		}, ClickEvent.getType());
	}

	@Override
	protected String getContentForHtmlPanel() {
		return Application.get().getResources().updateAppPrompt().getText();
	}
	
	@Override
	public void show() {
		super.show();
	}

}
