package superstartrek.client.bus;

import superstartrek.client.activities.appmenu.AppMenuHandler;
import superstartrek.client.activities.computer.ComputerHandler;
import superstartrek.client.activities.pwa.ApplicationLifecycleHandler;
import superstartrek.client.activities.sector.scan.ScanSectorHandler;

public class Commands {

	public final static Event<AppMenuHandler> APP_MENU_SHOW = new Event<>("APP_MENU_SHOW");
	public final static Event<AppMenuHandler> APP_MENU_HIDE = new Event<>("APP_MENU_HIDE");
	public final static Event<ComputerHandler> SHOW_COMPUTER = new Event<>("SHOW_COMPUTER");
	public final static Event<ComputerHandler> HIDE_COMPUTER = new Event<>("HIDE_COMPUTER");
	public final static Event<ScanSectorHandler> SCAN_SECTOR = new Event<>("SCAN_SECTOR");
	public final static Event<ApplicationLifecycleHandler> RELOAD_APP = new Event<>("RELOAD_APP");

}
