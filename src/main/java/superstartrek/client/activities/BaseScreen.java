package superstartrek.client.activities;

public abstract class BaseScreen<A extends Activity> extends BaseView<A>{

	public BaseScreen(Presenter<A> p) {
		super(p);
		hide();
		presenter.getApplication()._page.add(this);
		decorateScreen();
	}
	
	protected void decorateScreen() {
	}
	
	
}
