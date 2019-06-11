package superstartrek.client.activities.intro;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

import superstartrek.client.activities.BaseScreen;
import superstartrek.client.activities.CSS;
import superstartrek.client.utils.HtmlWidget;

public class IntroView extends BaseScreen<IntroPresenter>{
	
	Element eUpdateButton;
	Element eContinueSavedGameButton;

	public IntroView(IntroPresenter presenter) {
		super(presenter);
		getElement().setInnerHTML(presenter.getApplication().getResources().introScreen().getText());
		addStyleName("intro-screen");
		eUpdateButton = ((HtmlWidget)getWidget()).getElementById("cmd_check_for_updates");
		eContinueSavedGameButton = ((HtmlWidget)getWidget()).getElementById("cmd_continue_saved_game");
		getWidget().addHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Element e = event.getNativeEvent().getEventTarget().cast();
				if (eUpdateButton.isOrHasChild(e))
					presenter.onCheckForUpdatesButtonClicked();
				else if (eContinueSavedGameButton.isOrHasChild(e))
					presenter.onContinueGameButtonClicked();
			}
		}, ClickEvent.getType());
		getWidget().sinkEvents(Event.ONCLICK);
	}

	@Override
	protected Widget createWidgetImplementation() {
		return new HtmlWidget(DOM.createDiv());
	}
	
	public void showAppVersion(String version){
		DOM.getElementById("app-version").setInnerText("app version "+version);
	}
	
	public void disableUpdateCheckButton() {
		CSS.setEnabled(eUpdateButton, false);
	}

	public void enableUpdateCheckButton() {
		CSS.setEnabled(eUpdateButton, true);
	}
	
	public void setContinueGameButtonVisible(boolean visible){
		eContinueSavedGameButton.getStyle().setDisplay(visible?Display.BLOCK:Display.NONE);
	}

}
