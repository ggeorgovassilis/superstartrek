package superstartrek.client.bus;

import superstartrek.client.activities.appmenu.AppMenuHandler;
import superstartrek.client.activities.combat.CombatHandler;
import superstartrek.client.activities.computer.ComputerHandler;
import superstartrek.client.activities.computer.EnergyConsumptionHandler;
import superstartrek.client.activities.klingons.KlingonCloakingHandler;
import superstartrek.client.activities.messages.MessageHandler;
import superstartrek.client.activities.navigation.EnterpriseRepairedHandler;
import superstartrek.client.activities.navigation.NavigationHandler;
import superstartrek.client.activities.pwa.ApplicationLifecycleHandler;
import superstartrek.client.activities.sector.contextmenu.ContextMenuHideHandler;
import superstartrek.client.activities.sector.contextmenu.SectorSelectedHandler;
import superstartrek.client.activities.sector.scan.ScanSectorHandler;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.KeyPressedEventHandler;
import superstartrek.client.control.QuadrantActivationHandler;

/* We could reduce the number of events significantly if we wanted to because the caller specifies
 * which handler method to invoke. Reducing the number of events would have no functional impact, but
 * it would have a performance impact since all (empty) handler methods get called constantly.
 * 
 */
public class Events {

	// names are only for debugging purposes; can be disabled in Event class
	public final static Event<QuadrantActivationHandler> QUADRANT_ACTIVATED = new Event<>("QUADRANT_ACTIVATED");
	public final static Event<NavigationHandler> AFTER_ENTERPRISE_WARPED = new Event<>("AFTER_ENTERPRISE_WARPED");
	public final static Event<AppMenuHandler> APP_MENU_SHOW = new Event<>("APP_MENU_SHOW");
	public final static Event<AppMenuHandler> APP_MENU_HIDE = new Event<>("APP_MENU_HIDE");
	public final static Event<CombatHandler> ENTERPRISE_DAMAGED = new Event<>("ENTERPRISE_DAMAGED");
	public final static Event<CombatHandler> BEFORE_FIRE = new Event<>("BEFORE_FIRE");
	public final static Event<CombatHandler> AFTER_FIRE = new Event<>("AFTER_FIRE");
	public final static Event<GamePhaseHandler> KLINGON_TURN_STARTED = new Event<>("KLINGON_TURN_STARTED");
	public final static Event<KlingonCloakingHandler> KLINGON_CLOAKED = new Event<>("KLINGON_CLOAKED");
	public final static Event<KlingonCloakingHandler> KLINGON_UNCLOAKED = new Event<>("KLINGON_UNCLOAKED");
	public final static Event<ComputerHandler> SHOW_COMPUTER = new Event<>("SHOW_COMPUTER");
	public final static Event<ComputerHandler> HIDE_COMPUTER = new Event<>("HIDE_COMPUTER");
	public final static Event<EnergyConsumptionHandler> CONSUME_ENERGY = new Event<>("CONSUME_ENERGY");
	public final static Event<NavigationHandler> ENTERPRISE_DOCKED = new Event<>("ENTERPRISE_DOCKED");
	public final static Event<EnterpriseRepairedHandler> ENTERPRISE_REPAIRED = new Event<>("ENTERPRISE_REPAIRED");
	public final static Event<SectorSelectedHandler> SECTOR_SELECTED = new Event<>("SECTOR_SELECTED");
	public final static Event<CombatHandler> KLINGON_DESTROYED = new Event<>("KLINGON_DESTROYED");
	public final static Event<MessageHandler> MESSAGE_POSTED = new Event<>("MESSAGE_POSTED");
	public final static Event<MessageHandler> MESSAGE_READ = new Event<>("MESSAGE_READ");
	public final static Event<ContextMenuHideHandler> CONTEXT_MENU_HIDE = new Event<>("CONTEXT_MENU_HIDE");
	public final static Event<ScanSectorHandler> SCAN_SECTOR = new Event<>("SCAN_SECTOR");
	public final static Event<GamePhaseHandler> KLINGON_TURN_ENDED = new Event<>("KLINGON_TURN_ENDED");
	public final static Event<GamePhaseHandler> GAME_OVER = new Event<>("GAME_OVER");
	public final static Event<GamePhaseHandler> AFTER_TURN_STARTED = new Event<>("AFTER_TURN_STARTED");
	public final static Event<NavigationHandler> THING_MOVED = new Event<>("THING_MOVED");
	public final static Event<GamePhaseHandler> GAME_RESTART = new Event<>("GAME_RESTART");
	public final static Event<GamePhaseHandler> TURN_YIELDED = new Event<>("TURN_YIELDED");
	public final static Event<GamePhaseHandler> GAME_STARTED = new Event<>("GAME_STARTED");
	public final static Event<KeyPressedEventHandler> KEY_PRESSED = new Event<>("KEY_PRESSED");
	public final static Event<GamePhaseHandler> TURN_ENDED = new Event<>("TURN_ENDED");
	public final static Event<GamePhaseHandler> TURN_STARTED = new Event<>("TURN_STARTED");
	public final static Event<ApplicationLifecycleHandler> INFORMING_OF_INSTALLED_VERSION = new Event<>("INFORMING_OF_INSTALLED_VERSION");
	public final static Event<ApplicationLifecycleHandler> VERSION_CHECK_FAILED = new Event<>("VERSION_CHECK_FAILED");
	public final static Event<ApplicationLifecycleHandler> NEW_VERSION_AVAILABLE = new Event<>("NEW_VERSION_AVAILABLE");
	public final static Event<ApplicationLifecycleHandler> VERSION_IS_CURRENT = new Event<>("VERSION_IS_CURRENT");
	public final static Event<ApplicationLifecycleHandler> SHOW_APP_INSTALL_PROMPT = new Event<>("SHOW_APP_INSTALL_PROMPT");
	public final static Event<ApplicationLifecycleHandler> RELOAD_APP = new Event<>("RELOAD_APP");
}
