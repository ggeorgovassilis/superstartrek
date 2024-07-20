package superstartrek.client.activities.appinstallation;

import com.google.gwt.dom.client.Element;

import superstartrek.client.activities.popup.PopupViewImpl;
import superstartrek.client.screentemplates.ScreenTemplates;

public class AppInstallPromptViewImpl extends PopupViewImpl<AppInstallPromptPresenter>
		implements AppInstallPromptView{

	public AppInstallPromptViewImpl(AppInstallPromptPresenter presenter) {
		super(presenter);
	}

	@Override
	protected String getContentForHtmlPanel(ScreenTemplates templates) {
		return templates.getTemplateFor(ScreenTemplates.TemplateNames.installAppPrompt);
	}

	@Override
	protected void decorateWidget(ScreenTemplates templates, Element element) {
		super.decorateWidget(templates, element);
		element.setId("app-install-prompt");
	}
}
