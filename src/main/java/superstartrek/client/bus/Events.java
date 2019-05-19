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

/* We could reduce the number of events significantly if we wanted to because the caller specifies
 * which handler method to invoke. Reducing the number of events would have no functional impact, but
 * it would have a performance impact since all (empty) handler methods get called constantly.
 * 
 */
public class Events {

	public final static Event<NavigationHandler> AFTER_ENTERPRISE_WARPED = new Event<>();
	public final static Event<AppMenuHandler> APP_MENU_SHOW = new Event<>();
	public final static Event<AppMenuHandler> APP_MENU_HIDE = new Event<>();
	public final static Event<CombatHandler> ENTERPRISE_DAMAGED = new Event<>();
	public final static Event<CombatHandler> BEFORE_FIRE = new Event<>();
	public final static Event<CombatHandler> AFTER_FIRE = new Event<>();
	public final static Event<GamePhaseHandler> KLINGON_TURN_STARTED = new Event<>();
	public final static Event<KlingonCloakingHandler> KLINGON_CLOAKED = new Event<>();
	public final static Event<KlingonCloakingHandler> KLINGON_UNCLOAKED = new Event<>();
	public final static Event<ComputerHandler> SHOW_COMPUTER = new Event<>();
	public final static Event<ComputerHandler> HIDE_COMPUTER = new Event<>();
	public final static Event<EnergyConsumptionHandler> CONSUME_ENERGY = new Event<>();
	public final static Event<NavigationHandler> ENTERPRISE_DOCKED = new Event<>();
	public final static Event<EnterpriseRepairedHandler> ENTERPRISE_REPAIRED = new Event<>();
	public final static Event<SectorSelectedHandler> SECTOR_SELECTED = new Event<>();
	public final static Event<CombatHandler> KLINGON_DESTROYED = new Event<>();
	public final static Event<MessageHandler> MESSAGE_POSTED = new Event<>();
	public final static Event<MessageHandler> MESSAGE_READ = new Event<>();
	public final static Event<ContextMenuHideHandler> CONTEXT_MENU_HIDE = new Event<>();
	public final static Event<ScanSectorHandler> SCAN_SECTOR = new Event<>();
	public final static Event<GamePhaseHandler> KLINGON_TURN_ENDED = new Event<>();
	public final static Event<GamePhaseHandler> GAME_OVER = new Event<>();
	public final static Event<GamePhaseHandler> AFTER_TURN_STARTED = new Event<>();
	public final static Event<NavigationHandler> THING_MOVED = new Event<>();
	public final static Event<GamePhaseHandler> GAME_RESTART = new Event<>();
	public final static Event<GamePhaseHandler> TURN_YIELDED = new Event<>();
	public final static Event<GamePhaseHandler> GAME_STARTED = new Event<>();
	public final static Event<KeyPressedEventHandler> KEY_PRESSED = new Event<>();
	public final static Event<GamePhaseHandler> TURN_ENDED = new Event<>();
	public final static Event<GamePhaseHandler> TURN_STARTED = new Event<>();
	public final static Event<ApplicationLifecycleHandler> INFORMING_OF_INSTALLED_VERSION = new Event<>();
	public final static Event<ApplicationLifecycleHandler> VERSION_CHECK_FAILED = new Event<>();
	public final static Event<ApplicationLifecycleHandler> NEW_VERSION_AVAILABLE = new Event<>();
	public final static Event<ApplicationLifecycleHandler> VERSION_IS_CURRENT = new Event<>();
	public final static Event<ApplicationLifecycleHandler> SHOW_APP_INSTALL_PROMPT = new Event<>();
	public final static Event<ApplicationLifecycleHandler> RELOAD_APP = new Event<>();
}
