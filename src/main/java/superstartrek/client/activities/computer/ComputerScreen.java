package superstartrek.client.activities.computer;

import com.google.gwt.core.shared.GWT;

import superstartrek.client.Resources;
import superstartrek.client.activities.BaseActivity;
import superstartrek.client.activities.BaseScreen;
import superstartrek.client.activities.Presenter;

public class ComputerScreen extends BaseScreen{

	QuadrantScannerPresenter quadrantScannerPresenter;
	QuadrantScannerActivity quadrantScannerActivity;
	
	@Override
	protected void setupUI() {
		super.setupUI();
		getElement().setInnerHTML(Resources.INSTANCE.computerScreen().getText());
		presenter.getApplication().page.add(this);
		quadrantScannerPresenter = new QuadrantScannerPresenter(presenter.getApplication());
		GWT.log("ABC123");
		quadrantScannerActivity = new QuadrantScannerActivity(quadrantScannerPresenter);
		panel.add(quadrantScannerActivity,"quadrantscancontainer");
	}
	
	public ComputerScreen(Presenter presenter) {
		super(presenter);
	}
	
	@Override
	public void finishUiConstruction() {
		super.finishUiConstruction();
	}

}
