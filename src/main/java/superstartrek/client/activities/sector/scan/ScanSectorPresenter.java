package superstartrek.client.activities.sector.scan;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.computer.ComputerHandler.ComputerEvent;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;

public class ScanSectorPresenter extends BasePresenter<IScanSectorView> implements ScanSectorHandler{

	public ScanSectorPresenter(Application application) {
		super(application);
		application.events.addHandler(ScanSectorEvent.TYPE, this);
	}

	@Override
	public void scanSector(ScanSectorEvent event) {
		Quadrant q = event.getQuadrant();
		Thing thing = application.starMap.findThingAt(q, event.getLocation());
		String name = thing==null?"Nothing":thing.getName();
		view.setObjectName(name);
		view.setObjectLocation(event.getLocation().toString());
		view.setObjectQuadrant(q.getName());
		if (thing instanceof Vessel) {
			Vessel vessel = (Vessel)thing;
			view.setProperty("scan-report-shields", "scan-report-shields-value", "", "%"+vessel.getShields().percentage());
			if (vessel instanceof Klingon) {
				Klingon k = (Klingon)vessel;
				view.setProperty("scan-report-weapons", "scan-report-weapons-value", k.getDisruptor().isEnabled()?"":"damage-offline", k.getDisruptor().isEnabled()?"online":"offline");
				view.setProperty("scan-report-cloak", "scan-report-cloak-value", k.getCloak().isEnabled()?"":"damage-offline", k.getCloak().isEnabled()?"online":"offline");
			} else
			if (vessel instanceof Enterprise) {
				Enterprise e = (Enterprise)vessel;
				view.setProperty("scan-report-weapons", "scan-report-weapons-value", e.getPhasers().isEnabled()?"":"damage-offline", e.getPhasers().isEnabled()?"online":"offline");
			}
			view.setProperty("scan-report-engines", "scan-report-engines-value", vessel.getImpulse().isEnabled()?"":"damage-offline", vessel.getImpulse().isEnabled()?"online":"offline");
		} else {
			view.setProperty("scan-report-shields", "scan-report-shields-value", "hidden", "");
			view.setProperty("scan-report-weapons", "scan-report-weapons-value", "hidden", "");
			view.setProperty("scan-report-engines", "scan-report-engines-value", "hidden", "");
			view.setProperty("scan-report-cloak", "scan-report-cloak-value", "hidden", "");
		}
		view.show();
	}
	
	public void doneWithMenu() {
		if (!view.isVisible())
			return;
		view.hide();
		application.events.fireEvent(new ComputerEvent(ComputerEvent.Action.showScreen));
	}
	
	public void onCommandClicked(String cmd) {
		if ("screen-sectorscan-back".equals(cmd)) {
			doneWithMenu();
		}
	}

}
