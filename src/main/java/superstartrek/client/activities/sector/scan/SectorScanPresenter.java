package superstartrek.client.activities.sector.scan;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.computer.ComputerEvent;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;

public class SectorScanPresenter extends BasePresenter<ScanSectorActivity> implements ScanSectorHandler{

	public SectorScanPresenter(Application application) {
		super(application);
		application.events.addHandler(ScanSectorEvent.TYPE, this);
	}

	@Override
	public void scanSector(ScanSectorEvent event) {
		IScanSectorView v = (IScanSectorView)getView();
		Quadrant q = event.getQuadrant();
		Thing thing = application.starMap.findThingAt(q, event.getLocation().getX(), event.getLocation().getY());
		String name = thing==null?"Nothing":thing.getName();
		v.setObjectName(name);
		v.setObjectLocation(event.getLocation().toString());
		v.setObjectQuadrant(q.getName());
		if (thing instanceof Vessel) {
			Vessel vessel = (Vessel)thing;
			v.setProperty("scan-report-shields", "scan-report-shields-value", "", "%"+vessel.getShields().percentage());
			if (vessel instanceof Klingon) {
				Klingon k = (Klingon)vessel;
				v.setProperty("scan-report-weapons", "scan-report-weapons-value", k.getDisruptor().isEnabled()?"":"damage-offline", k.getDisruptor().isEnabled()?"online":"offline");
				v.setProperty("scan-report-cloak", "scan-report-cloak-value", k.getCloak().isEnabled()?"":"damage-offline", k.getCloak().isEnabled()?"online":"offline");
			}
			if (vessel instanceof Enterprise) {
				Enterprise e = (Enterprise)vessel;
				v.setProperty("scan-report-weapons", "scan-report-weapons-value", e.getPhasers().isEnabled()?"":"damage-offline", e.getPhasers().isEnabled()?"online":"offline");
			}
			v.setProperty("scan-report-engines", "scan-report-engines-value", vessel.getImpulse().isEnabled()?"":"damage-offline", vessel.getImpulse().isEnabled()?"online":"offline");
		} else {
			v.setProperty("scan-report-shields", "scan-report-shields-value", "hidden", "");
			v.setProperty("scan-report-weapons", "scan-report-weapons-value", "hidden", "");
			v.setProperty("scan-report-engines", "scan-report-engines-value", "hidden", "");
			v.setProperty("scan-report-cloak", "scan-report-cloak-value", "hidden", "");
		}
		getView().show();
	}
	
	public void doneWithMenu() {
		if (!getView().isVisible())
			return;
		getView().hide();
		application.events.fireEvent(new ComputerEvent(ComputerEvent.Action.showScreen));
	}
	
	public void onCommandClicked(String cmd) {
		if ("screen-sectorscan-back".equals(cmd)) {
			doneWithMenu();
		}
	}

}
