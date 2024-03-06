package superstartrek.client.activities.appinstallation;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import superstartrek.client.activities.PopupViewImpl;
import superstartrek.client.eventbus.Events;
import superstartrek.client.screentemplates.ScreenTemplates;

public class AppInstallPromptViewImpl extends PopupViewImpl<AppInstallPromptPresenter> implements AppInstallPromptView {

	public AppInstallPromptViewImpl(AppInstallPromptPresenter presenter) {
		super(presenter);
	}

	@Override
	protected String getContentForHtmlPanel(ScreenTemplates templates) {
		return templates.appInstallPrompt().getText();
	}

	@Override
	protected void decorateWidget(ScreenTemplates templates, Element element) {
		super.decorateWidget(templates, element);
		element.setId("app-install-prompt");
		presenter.getApplication().eventBus.addHandler(Events.INTERACTION, tag -> {
			switch (tag) {
			case "install-yes":
				presenter.userClickedInstallButton();
				break;
			case "install-no":
				presenter.cancelButtonClicked();
				break;
			case "install-never":
				presenter.userDoesntWantToInstallAppEver();
				break;
			}
		});
	}
}
