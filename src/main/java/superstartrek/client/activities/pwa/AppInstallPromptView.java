package superstartrek.client.activities.pwa;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

import superstartrek.client.activities.BaseView;
import superstartrek.client.activities.PopupView;
import superstartrek.client.utils.HtmlWidget;

public class AppInstallPromptView extends PopupView<AppInstallPromptPresenter> {

	public AppInstallPromptView(AppInstallPromptPresenter presenter) {
		super(presenter);
	}

	@Override
	protected String getContentForHtmlPanel() {
		return presenter.getApplication().getResources().appInstallPrompt().getText();
	}

	@Override
	public void decorateWidget() {
		super.decorateWidget();
		getElement().setId("app-install-prompt");
		addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
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
			}
		}, ClickEvent.getType());
	}
}
