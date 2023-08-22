package superstartrek.client.activities.pwa;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import superstartrek.client.activities.CSS;
import superstartrek.client.activities.PopupView;
import superstartrek.client.screentemplates.ScreenTemplates;

public class UpdateAppPromptView extends PopupView<UpdateAppPromptPresenter> {

	protected void disable(String id) {
		Element e = getElementById(id);
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
	protected void decorateWidget(ScreenTemplates templates, Element element) {
		super.decorateWidget(templates, element);
		element.setId("update-app-prompt");
		addHandler((event) -> {
			Element target = event.getNativeEvent().getEventTarget().cast();
			switch (target.getId()) {
			case "update-yes":
				presenter.acceptUpdateButtonClicked();
				break;
			case "update-no":
				presenter.cancelButtonClicked();
				break;
			}
		}, ClickEvent.getType());
	}

	@Override
	protected String getContentForHtmlPanel(ScreenTemplates templates) {
		return templates.updateAppPrompt().getText();
	}
}
