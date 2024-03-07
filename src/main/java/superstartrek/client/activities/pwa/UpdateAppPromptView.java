package superstartrek.client.activities.pwa;

import com.google.gwt.dom.client.Element;
import superstartrek.client.activities.PopupViewImpl;
import superstartrek.client.screentemplates.ScreenTemplates;
import superstartrek.client.utils.CSS;

public class UpdateAppPromptView extends PopupViewImpl<UpdateAppPromptPresenter> {

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
	}

	@Override
	protected String getContentForHtmlPanel(ScreenTemplates templates) {
		return templates.updateAppPrompt().getText();
	}
}
