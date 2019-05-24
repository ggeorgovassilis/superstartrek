package superstartrek.client.activities.sector.scan;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.PopupViewPresenter;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.bus.Commands;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;

public class ScanSectorPresenter extends BasePresenter<IScanSectorView> implements ScanSectorHandler, PopupViewPresenter<IScanSectorView>{

	public ScanSectorPresenter(Application application) {
		super(application);
		addHandler(Commands.SCAN_SECTOR, this);
	}

	@Override
	public void scanSector(Location location, Quadrant quadrant) {
		Quadrant q = quadrant;
		Thing thing = q.findThingAt(location);
		String name = thing==null?"Nothing":thing.getName();
		view.setObjectName(name);
		view.setObjectLocation(location.toString());
		view.setObjectQuadrant(q.getName());
		if (Vessel.is(thing)) {
			Vessel vessel = thing.as();
			view.setProperty("scan-report-shields", "scan-report-shields-value", "", "%"+vessel.getShields().percentage());
			if (Klingon.is(thing)) {
				Klingon k = vessel.as();
				view.setProperty("scan-report-weapons", "scan-report-weapons-value", k.getDisruptor().isEnabled()?"":"damage-offline", k.getDisruptor().isEnabled()?"online":"offline");
				view.setProperty("scan-report-cloak", "scan-report-cloak-value", k.getCloak().isEnabled()?"":"damage-offline", k.getCloak().isEnabled()?"online":"offline");
			} else
			if (Enterprise.is(thing)) {
				Enterprise e = vessel.as();
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
		fireEvent(Commands.SHOW_COMPUTER, (h)->h.showScreen());
	}
	
	public void onCommandClicked(String cmd) {
		if ("screen-sectorscan-back".equals(cmd)) {
			doneWithMenu();
		}
	}

	@Override
	public void userWantsToDismissPopup() {
		doneWithMenu();
	}

}
