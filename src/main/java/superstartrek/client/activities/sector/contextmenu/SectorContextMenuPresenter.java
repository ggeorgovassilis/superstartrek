package superstartrek.client.activities.sector.contextmenu;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.QuadrantIndex;
import superstartrek.client.activities.sector.scan.ScanSectorHandler;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.TurnEndedEvent;

public class SectorContextMenuPresenter extends BasePresenter<SectorContextMenuActivity> implements SectorSelectedHandler, GamePhaseHandler, ValueChangeHandler<String> {

	Location sector;
	Quadrant quadrant;
	boolean navigationEnabled = false;
	boolean phasersEnabled = false;
	boolean torpedosEngabled = false;
	boolean autoaimEnabled = false;

	public SectorContextMenuPresenter(Application application) {
		super(application);
		application.events.addHandler(SectorSelectedEvent.TYPE, this);
		application.events.addHandler(TurnEndedEvent.TYPE, this);
		application.addHistoryListener(this);
	}
	
	public void showMenuImmediatelly(int screenX, int screenY, Location sector, Quadrant quadrant) {
		SectorContextMenuPresenter.this.quadrant = quadrant;
		ISectorMenuView v = (ISectorMenuView) getView();
		Enterprise e = application.starMap.enterprise;
		//read dimensions before modifying the DOM to avoid re-layout
		int horizEmToPx = v.getMetricWidthInPx();
		int vertEmToPx = v.getMetricHeightInPx();
		navigationEnabled = e.canNavigateTo(new QuadrantIndex(quadrant, getApplication().starMap), sector);
		phasersEnabled = e.canFirePhaserAt(sector)==null;
		torpedosEngabled = e.getTorpedos().isEnabled() && e.getTorpedos().getValue() > 0;
		autoaimEnabled = e.getAutoAim().isEnabled() && e.getAutoAim().getBooleanValue();
		
		v.enableButton("cmd_navigate", navigationEnabled);
		v.enableButton("cmd_firePhasers", phasersEnabled);
		v.enableButton("cmd_fireTorpedos", torpedosEngabled);
		v.enableButton("cmd_toggleFireAtWill", autoaimEnabled);
		//if the menu is too close to the screen borders it might be cut off and not all buttons are visible
		//this is some heavy heuristics, because the menu has a "fixed" size (in em units)
		//that's empirical knowledge from the CSS
		int menuWidthEm = 12; 
		int menuHeightEm = 10; 
		int screen_width_em = application.browser.getWindowWidthPx() / horizEmToPx;
		
		int target_x_em = Math.max(screenX/horizEmToPx,menuWidthEm/2);
		target_x_em = Math.min(target_x_em,screen_width_em-menuWidthEm/2);
		int target_x_px = target_x_em*horizEmToPx;

		int target_y_em = Math.max(screenY/vertEmToPx,menuHeightEm/2);
		int target_y_px = target_y_em*vertEmToPx;
		v.setLocation(target_x_px, target_y_px);
		v.show();
	}

	public void showMenu(int screenX, int screenY, Location sector, Quadrant quadrant) {
		hideMenu(new ScheduledCommand() {
			
			@Override
			public void execute() {
				showMenuImmediatelly(screenX, screenY, sector, quadrant);
			}
		});
	}
	

	@Override
	public void onSectorSelected(SectorSelectedEvent event) {
		this.sector = event.getSector();
		showMenu(event.screenX, event.screenY, event.getSector(), event.getQuadrant());

	}
	
	public void onEscapePressed() {
		hideMenu(null);
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
				else if ("cmd_navigate".equals(command) && navigationEnabled)
					application.starMap.enterprise.navigateTo(sector);
				else if ("cmd_firePhasers".equals(command) && phasersEnabled)
					application.starMap.enterprise.firePhasersAt(sector, false);
				else if ("cmd_fireTorpedos".equals(command) && torpedosEngabled)
					application.starMap.enterprise.fireTorpedosAt(sector);
				else if ("cmd_toggleFireAtWill".equals(command) && autoaimEnabled)
					application.starMap.enterprise.toggleAutoAim();
			}
		});
	}

	@Override
	public void onTurnEnded(TurnEndedEvent evt) {
		hideMenu(null);
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		hideMenu(null);
	}

}
