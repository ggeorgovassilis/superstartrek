package superstartrek.client.activities.intro;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

import superstartrek.client.activities.BaseScreen;
import superstartrek.client.utils.HtmlWidget;

public class IntroView extends BaseScreen<IntroActivity>{

	public IntroView(IntroPresenter presenter) {
		super(presenter);
		getElement().setInnerHTML(presenter.getApplication().getResources().introScreen().getText());
	}

	@Override
	protected Widget createWidgetImplementation() {
		return new HtmlWidget(DOM.createDiv());
	}

}
