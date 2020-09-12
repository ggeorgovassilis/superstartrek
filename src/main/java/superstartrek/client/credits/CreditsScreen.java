package superstartrek.client.credits;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

import superstartrek.client.activities.BaseScreen;
import superstartrek.client.activities.CSS;
import superstartrek.client.utils.HtmlWidget;

public class CreditsScreen extends BaseScreen<CreditsPresenter>{
	
	public CreditsScreen(CreditsPresenter presenter) {
		super(presenter);
		addStyleName("credits-screen");
	}

	@Override
	protected Widget createWidgetImplementation() {
		return new HtmlWidget(DOM.createDiv(),presenter.getApplication().getResources().creditsScreen().getText());
	}
	
}
