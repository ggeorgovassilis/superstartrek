package superstartrek.client.activities.computer;

import superstartrek.client.Resources;
import superstartrek.client.activities.BaseScreen;
import superstartrek.client.activities.Presenter;

public class ComputerView extends BaseScreen<ComputerActivity>{

	QuadrantScannerPresenter quadrantScannerPresenter;
	QuadrantScannerView quadrantScannerActivity;
	
	@Override
	protected void setupUI() {
		super.setupUI();
		getElement().setInnerHTML(Resources.INSTANCE.computerScreen().getText());
		presenter.getApplication().page.add(this);
		quadrantScannerPresenter = new QuadrantScannerPresenter(presenter.getApplication());
		quadrantScannerActivity = new QuadrantScannerView(quadrantScannerPresenter);
		panel.add(quadrantScannerActivity,"quadrantscancontainer");
	}
	
	public ComputerView(Presenter<ComputerActivity> presenter) {
		super(presenter);
	}
	
}
