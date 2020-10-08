package superstartrek.client.activities.pwa;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import superstartrek.client.activities.PopupView;
import superstartrek.client.screentemplates.ScreenTemplates;

public class AppInstallPromptView extends PopupView<AppInstallPromptPresenter> implements IAppInstallPromptView {

	public AppInstallPromptView(AppInstallPromptPresenter presenter) {
		super(presenter);
	}

	@Override
	protected String getContentForHtmlPanel() {
		ScreenTemplates screenTemplates = presenter.getApplication().getScreenTemplates();
		return screenTemplates.appInstallPrompt().getText();
	}

	@Override
	public void decorateWidget() {
		super.decorateWidget();
		getElement().setId("app-install-prompt");
		addDomHandler((event) -> {
			Element e = event.getNativeEvent().getEventTarget().cast();
			switch (e.getId()) {
			case "install-yes":
				presenter.userClickedInstallButton();
				break;
			case "install-no":
				presenter.userWantsToDismissPopup();
				break;
			case "install-never":
				presenter.userDoesntWantToInstallAppEver();
				break;
			}
		}, ClickEvent.getType());
	}
}
