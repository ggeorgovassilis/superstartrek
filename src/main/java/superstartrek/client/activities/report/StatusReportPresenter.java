package superstartrek.client.activities.report;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.CSS;
import superstartrek.client.model.Enterprise;

public class StatusReportPresenter extends BasePresenter<StatusReportView> implements ValueChangeHandler<String>{

	public void updateView() {
		Enterprise enterprise = application.starMap.enterprise;
		view.setProperty("report_stardate", ""+application.starMap.getStarDate());
		view.setProperty("report_score", ""+application.scoreKeeper.getScore());
		view.setProperty("report_location", enterprise.getQuadrant().getName());
		view.setProperty("report_max_impulse", "%"+enterprise.getImpulse().percentageHealth());
		view.setProperty("report_shields", "%"+(enterprise.getShields().percentageHealth()));
		view.setProperty("report_phaser_power", "%"+(enterprise.getPhasers().percentageHealth()));
		view.setProperty("report_torpedos", ""+(enterprise.getTorpedos().getValue()));
		view.setProperty("report_energy", ""+(enterprise.getAntimatter().getValue()));
		view.setProperty("report_reactor", "%"+(enterprise.getReactor().percentageHealth()));
		view.setProperty("report_reactor_remaining", Math.floor(enterprise.getReactor().getValue())+" / "+Math.floor(enterprise.getReactor().getCurrentUpperBound()));
		view.setProperty("report_tactical_computer", enterprise.getAutoAim().isEnabled()?"online":"offline");
		view.setProperty("report_LRS", enterprise.getLrs().isEnabled()?"online":"offline");
		
		view.setOverlay("impulse", CSS.damageClass(enterprise.getImpulse().health()));
		view.setOverlay("phasers", CSS.damageClass(enterprise.getPhasers().health()));
		view.setOverlay("torpedobay", CSS.damageClass(enterprise.getTorpedos().health()));
		view.setOverlay("shields", CSS.damageClass(enterprise.getShields().health()));
	}
	
	public StatusReportPresenter(Application application) {
		super(application);
		application.browserAPI.addHistoryListener(this);
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		if ("statusreport".equals(event.getValue())) {
			updateView();
			view.show();
		}
		else
			view.hide();
	}
	

}
