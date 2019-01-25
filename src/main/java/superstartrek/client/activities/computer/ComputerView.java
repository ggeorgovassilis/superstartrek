package superstartrek.client.activities.computer;

import superstartrek.client.Resources;
import superstartrek.client.activities.BaseScreen;
import superstartrek.client.activities.computer.quadrantscanner.QuadrantScannerPresenter;
import superstartrek.client.activities.computer.quadrantscanner.QuadrantScannerView;
import superstartrek.client.activities.computer.srs.SRSPresenter;
import superstartrek.client.activities.computer.srs.SRSView;

public class ComputerView extends BaseScreen<ComputerActivity>{

	QuadrantScannerPresenter quadrantScannerPresenter;
	QuadrantScannerView quadrantScannerActivity;
	SRSPresenter srsPresenter;
	
	@Override
	protected void setupUI() {
		super.setupUI();
		getElement().setInnerHTML(Resources.INSTANCE.computerScreen().getText());
		presenter.getApplication().page.add(this);
		quadrantScannerPresenter = new QuadrantScannerPresenter(presenter.getApplication());
		quadrantScannerActivity = new QuadrantScannerView(quadrantScannerPresenter);
		panel.add(quadrantScannerActivity,"quadrantscancontainer");
		
		srsPresenter = new SRSPresenter(presenter.getApplication());
		SRSView srsView = new SRSView(srsPresenter);
		panel.addAndReplaceElement(srsView, "shortrangescan");
	}
	
	public ComputerView(ComputerPresenter presenter) {
		super(presenter);
	}
	
}
