package superstartrek.client.activities;

public abstract class BaseScreen<P extends Presenter<?>> extends BaseView<P>{

	public BaseScreen(P p) {
		super(p);
		hide();
		presenter.getApplication()._page.add(this);
		//from a performance POV it'd be better if decorateScreen() were called before the widget is added to the root panel
		//but many implementations depend on getElementById() which words only if the widget is attached
		decorateScreen();
	}
	
	protected void decorateScreen() {
	}
	
	
}
