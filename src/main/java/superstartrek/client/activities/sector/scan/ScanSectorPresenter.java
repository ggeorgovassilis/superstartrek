package superstartrek.client.activities.sector.scan;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.HandlerRegistration;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.computer.ComputerEvent;
import superstartrek.client.activities.glasspanel.GlassPanelEvent;
import superstartrek.client.activities.glasspanel.GlassPanelEvent.Action;
import superstartrek.client.activities.glasspanel.GlassPanelHandler;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;

public class ScanSectorPresenter extends BasePresenter<ScanSectorActivity> implements ScanSectorHandler, GlassPanelHandler{

	HandlerRegistration glassPanelHandler;
	
	public ScanSectorPresenter(Application application) {
		super(application);
		application.events.addHandler(ScanSectorEvent.TYPE, this);
	}

	@Override
	public void scanSector(ScanSectorEvent event) {
		getView().show();
		application.events.fireEvent(new GlassPanelEvent(Action.show));
		glassPanelHandler = application.events.addHandler(GlassPanelEvent.TYPE, this);
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
	}
	
	public void doneWithMenu() {
		if (!getView().isVisible())
			return;
		GWT.log("doneWithMenu");
		getView().hide();
		if (glassPanelHandler!=null)
			glassPanelHandler.removeHandler();
		glassPanelHandler = null;
		application.events.fireEvent(new ComputerEvent(ComputerEvent.Action.showScreen));
	}
	
	public void onCommandClicked(String cmd) {
		if ("screen-sectorscan-back".equals(cmd)) {
			doneWithMenu();
		}
	}

	@Override
	public void glassPanelShown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void glassPanelHidden() {
		doneWithMenu();
	}

	@Override
	public void glassPanelClicked() {
		doneWithMenu();
	}

}
