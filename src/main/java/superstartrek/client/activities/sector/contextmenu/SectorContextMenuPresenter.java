package superstartrek.client.activities.sector.contextmenu;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.activities.sector.scan.ScanSectorHandler;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.TurnEndedEvent;

public class SectorContextMenuPresenter extends BasePresenter<ISectorContextMenuView>
		implements SectorSelectedHandler, GamePhaseHandler, ValueChangeHandler<String>, ContextMenuHideHandler {

	Location sector;
	Quadrant quadrant;
	Map<String, Boolean> buttonsEnabled = new HashMap<>();

	public SectorContextMenuPresenter(Application application) {
		super(application);
		addHandler(SectorSelectedEvent.TYPE, this);
		addHandler(TurnEndedEvent.TYPE, this);
		addHandler(ContextMenuHideEvent.TYPE, this);
		application.browserAPI.addHistoryListener(this);

		buttonsEnabled.put("cmd_navigate", false);
		buttonsEnabled.put("cmd_scanSector", true);
		buttonsEnabled.put("cmd_firePhasers", false);
		buttonsEnabled.put("cmd_fireTorpedos", false);
	}

	public void showMenuImmediatelly(int screenX, int screenY, Location sector, Quadrant quadrant) {
		SectorContextMenuPresenter.this.quadrant = quadrant;
		Enterprise e = application.starMap.enterprise;
		// read dimensions before modifying the DOM to avoid re-layout
		int horizEmToPx = view.getMetricWidthInPx();
		int vertEmToPx = view.getMetricHeightInPx();

		buttonsEnabled.put("cmd_navigate",
				e.canNavigateTo(sector));
		buttonsEnabled.put("cmd_firePhasers", e.canFirePhaserAt(sector) == null);
		buttonsEnabled.put("cmd_fireTorpedos", e.getTorpedos().isEnabled() && e.getTorpedos().getValue() > 0);
		for (String cmd : buttonsEnabled.keySet())
			view.enableButton(cmd, buttonsEnabled.get(cmd));
		// if the menu is too close to the screen borders it might be cut off and not
		// all buttons are visible
		// this is some heavy heuristics, because the menu has a "fixed" size (in em
		// units)
		// that's empirical knowledge from the CSS
		int menuWidthEm = 8;
		int menuHeightEm = 5;
		int screen_width_em = application.browserAPI.getWindowWidthPx() / horizEmToPx;

		int target_x_em = Math.max(screenX / horizEmToPx, menuWidthEm / 2);
		target_x_em = Math.min(target_x_em, screen_width_em - menuWidthEm);
		int target_x_px = target_x_em * horizEmToPx;

		int target_y_em = Math.max(screenY / vertEmToPx, menuHeightEm / 2);
		int target_y_px = target_y_em * vertEmToPx;
		view.setLocation(target_x_px, target_y_px);
		view.show();
	}

	public void showMenu(int screenX, int screenY, Location sector, Quadrant quadrant) {
		hideMenu(() -> showMenuImmediatelly(screenX, screenY, sector, quadrant));
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
		view.hide(callback);
	}

	public void onMenuClicked() {
		hideMenu(null);
	}

	public void onCommandClicked(String command) {
		Enterprise enterprise = application.starMap.enterprise;
		if (buttonsEnabled.get(command) == true)
			hideMenu(() -> {
				switch (command) {
				case "cmd_scanSector":
					application.events.fireEvent(new ScanSectorHandler.ScanSectorEvent(sector, quadrant));
					break;
				case "cmd_navigate":
					enterprise.navigateTo(sector);
					break;
				case "cmd_firePhasers":
					enterprise.firePhasersAt(sector, false);
					break;
				case "cmd_fireTorpedos":
					enterprise.fireTorpedosAt(sector);
					break;
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

	@Override
	public void onMenuHide() {
		if (view.isVisible())
			hideMenu(null);
	}

}
