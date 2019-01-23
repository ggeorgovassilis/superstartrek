package superstartrek.client.activities;

import com.google.gwt.core.shared.GWT;

public abstract class BaseScreen extends BaseActivity{

	public BaseScreen(Presenter p) {
		super(p);
		setupUI();
		hide();
	}
	
	protected void setupUI() {
	}
	
	@Override
	public void finishUiConstruction() {
		GWT.log("1");
		presenter.getApplication().page.add(this);
		GWT.log("2");
	}
}
