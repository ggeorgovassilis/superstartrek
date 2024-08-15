package superstartrek.client.activities.report;

import superstartrek.client.activities.ActivityChangedHandler;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.eventbus.Events;
import superstartrek.client.utils.CSS;
import superstartrek.client.vessels.Enterprise;

public class StatusReportPresenter extends BasePresenter<StatusReportScreen> implements ActivityChangedHandler {

	public void updateView() {
		Enterprise enterprise = getEnterprise();
		view.setProperty("report_stardate", "" + getApplication().starMap.getStarDate(), false);
		view.setProperty("report_score", "" + getApplication().scoreKeeper.getScore(), false);
		view.setProperty("report_location", enterprise.getQuadrant().getName(), false);
		view.setProperty("report_max_impulse", "%" + Math.floor(enterprise.getImpulse().percentageHealth()),
				enterprise.getImpulse().percentageHealth() < 100);
		view.setProperty("report_shields", "%" + Math.floor(enterprise.getShields().percentageHealth()),
				enterprise.getShields().percentageHealth() < 100);
		view.setProperty("report_phaser_power", "%" + Math.floor(enterprise.phasers.percentageHealth()),
				enterprise.phasers.percentageHealth() < 100);
		view.setProperty("report_torpedos",
				"" + (enterprise.torpedos.getValue() + " ("
						+ (enterprise.torpedos.isOperational() ? "online" : "offline") + ")"),
				!enterprise.torpedos.isOperational());
		view.setProperty("report_energy", "" + Math.floor(enterprise.getAntimatter().getValue()),
				enterprise.getAntimatter().getValue() < 100);
		view.setProperty("report_reactor", "%" + (Math.floor(enterprise.reactor.percentageHealth())),
				enterprise.reactor.percentageHealth() < 100);
		view.setProperty("report_reactor_remaining", Math.floor(enterprise.reactor.getValue()) + " / "
				+ Math.floor(enterprise.reactor.getCurrentUpperBound()), false);
		view.setProperty("report_tactical_computer", enterprise.autoAim.isOperational() ? "online" : "offline",
				!enterprise.autoAim.isOperational());
		view.setProperty("report_LRS", enterprise.lrs.isOperational() ? "online" : "offline",
				!enterprise.lrs.isOperational());
		view.setProperty("report_warp", enterprise.warpDrive.isOperational() ? "online" : "offline",
				!enterprise.warpDrive.isOperational());

		view.setOverlay("impulse", CSS.damageClass(enterprise.getImpulse()));
		view.setOverlay("phasers", CSS.damageClass(enterprise.phasers));
		view.setOverlay("torpedobay", CSS.damageClass(enterprise.torpedos));
		view.setOverlay("shields", CSS.damageClass(enterprise.getShields()));
		view.setOverlay("warp", CSS.damageClass(enterprise.warpDrive));
	}

	public StatusReportPresenter() {
		addHandler(Events.ACTIVITY_CHANGED);
	}

	@Override
	public void onActivityChanged(String activity) {
		if ("statusreport".equals(activity)) {
			updateView();
			view.show();
		} else
			view.hide();
	}

}
