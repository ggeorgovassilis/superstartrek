package superstartrek.client.activities.pwa;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import superstartrek.client.Application;
import superstartrek.client.activities.PopupView;
import superstartrek.client.activities.Presenter;

public class UpdateAppPromptView extends PopupView<UpdateAppActivity>{
	
	public void disableButtons() {
		DOM.getElementById("update-yes").setAttribute("disabled", "disabled");
		DOM.getElementById("update-no").setAttribute("disabled", "disabled");
	}

	public UpdateAppPromptView(Presenter<UpdateAppActivity> presenter) {
		super(presenter);
		addStyleName("install-pwa-prompt");
		sinkEvents(Event.ONCLICK);
		addHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Element target = event.getNativeEvent().getEventTarget().cast();
				String id = target.getId();
				UpdateAppPromptPresenter presenter = getPresenter();
				if ("update-yes".equals(id)) {
					presenter.acceptUpdateButtonClicked();
				}
				if ("update-no".equals(id)) {
					presenter.dismissButtonClicked();
					
				}
			}
		}, ClickEvent.getType());
	}

	@Override
	protected String getContentForHtmlPanel() {
		return Application.get().getResources().updateAppScreen().getText();
	}

}
