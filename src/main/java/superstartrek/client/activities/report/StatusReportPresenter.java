package superstartrek.client.activities.report;

import superstartrek.client.activities.ActivityChangedHandler;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.eventbus.Events;
import superstartrek.client.utils.CSS;
import superstartrek.client.vessels.Enterprise;

public class StatusReportPresenter extends BasePresenter<StatusReportView> implements ActivityChangedHandler {

	public void updateView() {
		Enterprise enterprise = getEnterprise();
		view.setProperty("report_stardate", "" + getApplication().starMap.getStarDate(), false);
		view.setProperty("report_score", "" + getApplication().scoreKeeper.getScore(), false);
		view.setProperty("report_location", enterprise.getQuadrant().getName(), false);
		view.setProperty("report_max_impulse", "%" + Math.floor(enterprise.getImpulse().percentageHealth()),
				enterprise.getImpulse().percentageHealth() < 100);
		view.setProperty("report_shields", "%" + Math.floor(enterprise.getShields().percentageHealth()),
				enterprise.getShields().percentageHealth() < 100);
		view.setProperty("report_phaser_power", "%" + Math.floor(enterprise.getPhasers().percentageHealth()),
				enterprise.getPhasers().percentageHealth() < 100);
		view.setProperty("report_torpedos", "" + (enterprise.getTorpedos().getValue()),
				enterprise.getTorpedos().getValue() == 0);
		view.setProperty("report_energy", "" + Math.floor(enterprise.getAntimatter().getValue()),
				enterprise.getAntimatter().getValue() < 100);
		view.setProperty("report_reactor", "%" + (Math.floor(enterprise.getReactor().percentageHealth())),
				enterprise.getReactor().percentageHealth() < 100);
		view.setProperty("report_reactor_remaining", Math.floor(enterprise.getReactor().getValue()) + " / "
				+ Math.floor(enterprise.getReactor().getCurrentUpperBound()), false);
		view.setProperty("report_tactical_computer", enterprise.getAutoAim().isOperational() ? "online" : "offline",
				!enterprise.getAutoAim().isOperational());
		view.setProperty("report_LRS", enterprise.getLrs().isOperational() ? "online" : "offline",
				!enterprise.getLrs().isOperational());
		view.setProperty("report_warp", enterprise.getWarpDrive().isOperational() ? "online" : "offline",
				!enterprise.getWarpDrive().isOperational());

		view.setOverlay("impulse", CSS.damageClass(enterprise.getImpulse()));
		view.setOverlay("phasers", CSS.damageClass(enterprise.getPhasers()));
		view.setOverlay("torpedobay", CSS.damageClass(enterprise.getTorpedos()));
		view.setOverlay("shields", CSS.damageClass(enterprise.getShields()));
		view.setOverlay("warp", CSS.damageClass(enterprise.getWarpDrive()));
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
