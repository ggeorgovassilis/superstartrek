package superstartrek.client.activities.sector.scan;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.computer.ComputerEvent;
import superstartrek.client.activities.glasspanel.GlassPanelEvent;
import superstartrek.client.activities.glasspanel.GlassPanelEvent.Action;

public class ScanSectorPresenter extends BasePresenter<ScanSectorActivity> implements ScanSectorHandler{

	public ScanSectorPresenter(Application application) {
		super(application);
		application.events.addHandler(ScanSectorEvent.TYPE, this);
	}

	@Override
	public void scanSector(ScanSectorEvent event) {
		getView().show();
		application.events.fireEvent(new GlassPanelEvent(Action.show));
	}
	
	public void onCommandClicked(String cmd) {
		if ("screen-sectorscan-back".equals(cmd)) {
			getView().hide();
			application.events.fireEvent(new ComputerEvent(ComputerEvent.Action.showScreen));
		}
	}

}
