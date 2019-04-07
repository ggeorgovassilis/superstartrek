package superstartrek.client.activities;

public abstract class BaseScreen<A extends Activity> extends BaseView<A>{

	public BaseScreen(Presenter<A> p) {
		super(p);
		setupCompositeUI();
		hide();
	}
	
	protected void setupCompositeUI() {
	}
	
	@Override
	public void finishUiConstruction() {
		presenter.getApplication()._page.add(this);
	}
	
}
