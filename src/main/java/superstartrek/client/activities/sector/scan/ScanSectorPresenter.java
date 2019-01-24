package superstartrek.client.activities.sector.scan;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.computer.ComputerEvent;
import superstartrek.client.activities.glasspanel.GlassPanelEvent;
import superstartrek.client.activities.glasspanel.GlassPanelEvent.Action;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Thing;

public class ScanSectorPresenter extends BasePresenter<ScanSectorActivity> implements ScanSectorHandler{

	public ScanSectorPresenter(Application application) {
		super(application);
		application.events.addHandler(ScanSectorEvent.TYPE, this);
	}

	@Override
	public void scanSector(ScanSectorEvent event) {
		getView().show();
		application.events.fireEvent(new GlassPanelEvent(Action.show));
		ScanSectorView v = (ScanSectorView)getView();
		Quadrant q = event.getQuadrant();
		Thing thing = application.starMap.findThingAt(q, event.getLocation().getX(), event.getLocation().getY());
		String name = thing==null?"Nothing":thing.getName();
		v.setObjectName(name);
		v.setObjectLocation(event.getLocation().toString());
		v.setObjectQuadrant(q.getName());
	}
	
	public void onCommandClicked(String cmd) {
		if ("screen-sectorscan-back".equals(cmd)) {
			getView().hide();
			application.events.fireEvent(new ComputerEvent(ComputerEvent.Action.showScreen));
		}
	}

}
