package superstartrek.client.activities.sector.contextmenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.activities.sector.scan.ScanSectorHandler;

public class SectorMenuPresenter extends BasePresenter<SectorMenuActivity> implements SectorSelectedHandler {

	Location sector;
	Quadrant quadrant;

	public SectorMenuPresenter(Application application) {
		super(application);
		application.events.addHandler(SectorSelectedEvent.TYPE, this);
	}

	public void showMenu(int screenY, Location sector, Quadrant quadrant) {
		this.quadrant = quadrant;
		ISectorMenuView v = (ISectorMenuView) getView();
		Enterprise e = application.starMap.enterprise;
		v.enableButton("cmd_navigate", e.getImpulse().isEnabled());
		v.enableButton("cmd_firePhasers", e.getPhasers().isEnabled());
		v.enableButton("cmd_fireTorpedos", e.getTorpedos().isEnabled() && e.getTorpedos().getValue() > 0);
		v.enableButton("cmd_toggleFireAtWill", e.getAutoAim().isEnabled() && e.getAutoAim().getBooleanValue());

		v.setLocation(0, screenY);
		v.show();
	}

	@Override
	public void onSectorSelected(SectorSelectedEvent event) {
		this.sector = event.getSector();
		showMenu(event.screenY, event.getSector(), event.getQuadrant());

	}

	protected void hideMenu(ScheduledCommand callback) {
		getView().hide(callback);
	}

	public void onMenuClicked() {
		hideMenu(null);
	}

	public void onCommandClicked(String command) {
		hideMenu(new ScheduledCommand() {

			@Override
			public void execute() {
				if ("cmd_scanSector".equals(command))
					application.events.fireEvent(new ScanSectorHandler.ScanSectorEvent(sector, quadrant));
				else if ("cmd_navigate".equals(command))
					application.starMap.enterprise.navigateTo(sector);
				else if ("cmd_firePhasers".equals(command))
					application.starMap.enterprise.firePhasersAt(sector, false);
				else if ("cmd_fireTorpedos".equals(command))
					application.starMap.enterprise.fireTorpedosAt(sector);
				else if ("cmd_toggleFireAtWill".equals(command))
					application.starMap.enterprise.toggleAutoAim();
			}
		});
	}

}
