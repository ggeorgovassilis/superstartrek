package superstartrek.client.activities.report;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.model.Enterprise;

public class StatusReportPresenter extends BasePresenter<StatusReportActivity>{

	public void updateView() {
		StatusReportView view = (StatusReportView)getView();
		Enterprise enterprise = application.starMap.enterprise;
		view.setProperty("report_stardate", ""+application.starMap.getStarDate());
		view.setProperty("report_location", enterprise.getQuadrant().getName());
		view.setProperty("report_max_impulse", "%"+enterprise.getImpulse().percentageHealth());
		view.setProperty("report_shields", "%"+(enterprise.getShields().percentageHealth()));
		view.setProperty("report_phaser_power", "%"+(enterprise.getPhasers().percentageHealth()));
		view.setProperty("report_torpedos", "%"+(enterprise.getTorpedos().getValue()));
	}
	
	public StatusReportPresenter(Application application) {
		super(application);
		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				if ("statusreport".equals(event.getValue())) {
					updateView();
					getView().show();
				}
				else
					getView().hide();
			}
		});
	}
	

}
