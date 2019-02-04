package superstartrek.client.activities.computer;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.glasspanel.GlassPanelEvent;
import superstartrek.client.activities.glasspanel.GlassPanelEvent.Action;
import superstartrek.client.activities.navigation.EnterpriseWarpedEvent;
import superstartrek.client.activities.navigation.EnterpriseWarpedHandler;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarBase;
import superstartrek.client.model.StarMap;

public class ComputerPresenter extends BasePresenter<ComputerActivity> implements ComputerHandler, TurnStartedHandler{

	public ComputerPresenter(Application application) {
		super(application);
		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				if ("computer".equals(event.getValue()))
					showScreen();
				else
					hideScreen();
			}
		});
		application.events.addHandler(ComputerEvent.TYPE, this);
		application.events.addHandler(TurnStartedEvent.TYPE, this);
	}
	
	public void onSkipButtonClicked() {
		application.endTurnAfterThis();
	}

	@Override
	public void showScreen() {
		application.events.fireEvent(new GlassPanelEvent(Action.hide));
		getView().show();
		((ComputerView)getView()).showStarDate(""+application.starMap.getStarDate());
	}

	@Override
	public void hideScreen() {
		getView().hide();
	}
	
	public void onDockInStarbaseButtonClicked() {
		StarMap map = application.starMap;
		Enterprise enterprise = map.enterprise;
		Quadrant q = enterprise.getQuadrant();
		Location loc = application.starMap.findFreeSpotAround(q, q.getStarBase(),1);
		if (loc==null) {
			application.message("No space around starbase");
			return;
		}
		enterprise._navigateTo(loc);
		enterprise.dockAtStarbase(q.getStarBase());
	}
	
	public void updateDockInStarbaseButton() {
		Enterprise enterprise = application.starMap.enterprise;
		Quadrant q = enterprise.getQuadrant();
		StarBase starBase = q.getStarBase();
		boolean visible = starBase!=null && (q.getKlingons().isEmpty() || StarMap.distance(enterprise, starBase)<2);
		((ComputerView)getView()).setDockInStarbaseButtonVisibility(visible);
	}

	@Override
	public void onTurnStarted(TurnStartedEvent evt) {
		ComputerView view = (ComputerView)getView();
		view.showStarDate(""+application.starMap.getStarDate());
		updateDockInStarbaseButton();
	}

}
