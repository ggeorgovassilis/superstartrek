package superstartrek.client.activities;

import com.google.gwt.dom.client.Element;

import superstartrek.client.Application;
import superstartrek.client.screentemplates.ScreenTemplates;

@SuppressWarnings("rawtypes")
public abstract class BaseScreen<P extends Presenter> extends BaseView<P>{


	public BaseScreen(P p) {
		super(p);
		hide();
		Application app = presenter.getApplication();
		app.browserAPI.addToPage(this);
		//from a performance POV it'd be better if decorateScreen() were called before the widget is added to the root panel
		//but many implementations depend on getElementById() which works only if the widget is attached
		decorateScreen(app.getScreenTemplates(), getElement());
		getElement().addClassName("screen");
	}
	
	protected void decorateScreen(ScreenTemplates templates, Element element) {
	}
	
}
