package superstartrek.client.vessels;

import static superstartrek.client.eventbus.Events.*;

import java.util.List;

import superstartrek.client.Application;
import superstartrek.client.activities.messages.MessagesMixin;
import superstartrek.client.activities.navigation.NavigationHandler;
import superstartrek.client.activities.navigation.PathFinder;
import superstartrek.client.activities.navigation.PathFinderImpl;
import superstartrek.client.control.GamePhaseHandler;
import superstartrek.client.control.QuadrantActivationHandler;
import superstartrek.client.eventbus.EventsMixin;
import superstartrek.client.space.Constants;
import superstartrek.client.space.Location;
import superstartrek.client.space.Quadrant;
import superstartrek.client.space.Setting;
import superstartrek.client.space.StarMap;
import superstartrek.client.space.Thing;
import superstartrek.client.utils.BaseMixin;
import superstartrek.client.utils.BrowserAPI;

public class Klingon extends Vessel
		implements EventsMixin, CombatHandler, GamePhaseHandler, NavigationHandler, BaseMixin, QuadrantActivationHandler, MessagesMixin {

	public static enum ShipClass {

		Raider("a Klingon raider", 50, 10,
				"<div class=vessel><span class=bridge>c</span><span class=fuselage>-</span><span class=wings>}</span></div>",
				100),
		BirdOfPrey("a Bird-of-prey", 100, 20,
				"<div class=vessel><span class=bridge>C</span><span class=fuselage>-</span><span class=wings>D</span></div>",
				300);

		ShipClass(String label, int shields, int disruptor, String symbol, int xp) {
			this.label = label;
			this.shields = shields;
			this.symbol = symbol;
			this.disruptor = disruptor;
			this.xp = xp;
		}

		public final String label;
		public final int shields;
		public final String symbol;
		public final int disruptor;
		public final int xp;
	}

	Setting disruptor;
	Setting cloak;
	int xp;
	String css;
	final ShipClass sc;

	public Klingon() {
		this(ShipClass.BirdOfPrey);
	}

	public Klingon(ShipClass c) {
		super(new Setting(1), new Setting(c.shields));
		this.sc = c;
		this.xp = c.xp;
		cloak = new Setting(1);
		setCss("klingon");
		this.disruptor = new Setting(c.disruptor);
		addHandler(QUADRANT_ACTIVATED, this);
		addHandler(GAME_RESTART, this);
		addHandler(GAME_STARTED, this);
	}
	
	@Override
	public String getName() {
		return sc.label;
	}
	
	@Override
	public String getSymbol() {
		return sc.symbol;
	}

	public int getXp() {
		return xp;
	}
	
	void setCss(String css) {
		this.css = css;
	}

	/*
	 * for performance reasons, only Klingons in Enterprise's quadrant need to
	 * process events, so we're registering them whenever Enterprise enters a
	 * quadrant and unregister them when it leaves.
	 */
	public void registerActionHandlers() {
		addHandler(BEFORE_FIRE, this);
		addHandler(KLINGON_TURN_STARTED, this);
	}

	public void unregisterActionHandlers() {
		removeHandler(BEFORE_FIRE, this);
		removeHandler(KLINGON_TURN_STARTED, this);
	}

	public boolean canCloak() {
		return !cloak.isBroken();
	}

	public Setting getCloak() {
		return cloak;
	}

	public void uncloak() {
		cloak.setValue(0);
		setCss("klingon");
		message(getName() + " uncloaked at " + this.getLocation(), "klingon-uncloaked");
		fireEvent(KLINGON_UNCLOAKED, (h) -> h.klingonUncloaked(Klingon.this));
	}

	public Setting getDisruptor() {
		return disruptor;
	}

	public boolean hasClearShotAt(Quadrant index, Location target, Enterprise enterprise, StarMap map) {
		if (StarMap.within_distance(target, getLocation(), Constants.KLINGON_DISRUPTOR_RANGE_SECTORS)) {
			List<Thing> obstacles = StarMap.findObstaclesInLine(index, getLocation(), target, 2);
			obstacles.remove(enterprise);
			obstacles.remove(this);
			return obstacles.isEmpty();
		}
		return false;
	}

	public void repositionKlingon(Quadrant quadrant) {
		if (!getImpulse().isOperational())
			return;
		StarMap map = getStarMap();
		Enterprise enterprise = map.enterprise;
		// no need to move if distance is <=2 and Klingon has a clear shot at the
		// Enterprise
		if (hasClearShotAt(quadrant, enterprise.getLocation(), enterprise, map))
			return;
		PathFinder pathFinder = new PathFinderImpl();
		// path includes start and end
		pathFinder.load(quadrant);
		List<Location> path = pathFinder.findPathBetween(this.getLocation(), enterprise.getLocation());
		if (path.isEmpty())
			return;
		// path used to contain origin sector (old a* impl); it doesn't anymore, that's
		// why MAX_SECTOR_SPEED-1
		Location sector = path.get(Math.max(0, Math.min(Constants.KLINGON_MAX_SECTOR_SPEED - 1, path.size() - 2)));
		jumpTo(sector);
	}

	public void jumpTo(Location dest) {
		Quadrant quadrant = getActiveQuadrant();
		Location currentLocation = getLocation();
		quadrant.remove(this);
		setLocation(dest);
		quadrant.add(this);
		fireEvent(THING_MOVED, (h) -> h.thingMoved(Klingon.this, quadrant, currentLocation, quadrant, dest));
	}

	public void fireOnEnterprise(Quadrant index) {
		if (getDisruptor().isBroken())
			return;
		StarMap map = getStarMap();
		Enterprise enterprise = map.enterprise;
		if (!StarMap.within_distance(this, enterprise, Constants.KLINGON_DISRUPTOR_RANGE_SECTORS))
			return;
		if (!hasClearShotAt(index, enterprise.getLocation(), enterprise, map))
			return;
		if (!isVisible())
			uncloak();
		fireEvent(BEFORE_FIRE, (h) -> h.onFire(enterprise.getQuadrant(), Klingon.this, enterprise, Weapon.disruptor,
				disruptor.getValue(), true, partTarget.none));
		fireEvent(AFTER_FIRE, (h) -> h.afterFire(enterprise.getQuadrant(), Klingon.this, enterprise, Weapon.disruptor,
				disruptor.getValue(), true));
	}

	public void cloak() {
		getCloak().setValue(true);
		fireEvent(KLINGON_CLOAKED, (h) -> h.klingonCloaked(Klingon.this));
		message(getName() + " cloaked at " + this.getLocation(), "klingon-uncloaked");
	}

	public void flee(Quadrant index) {
		if (canCloak() && isVisible())
			cloak();
		if (!getImpulse().isOperational() || getImpulse().getValue() < 1)
			return;
		Application app = getApplication();
		StarMap starMap = app.starMap;
		Enterprise enterprise = starMap.enterprise;
		Location enterpriseLocation = enterprise.getLocation();
		double distance = StarMap.distance(getLocation(), enterpriseLocation);
		int triesLeft = 3;
		while (triesLeft-- > 0) {
			Location loc = starMap.findFreeSpotAround(index, getLocation(), 1 + (int) getImpulse().getValue());
			if (loc != null) {
				double newDistance = StarMap.distance(enterpriseLocation, loc);
				if (newDistance > distance) {
					triesLeft = 0;
					jumpTo(loc);
				}
			}
		}
	}

	@Override
	public void onKlingonTurnStarted() {
		// Reminder: only Klingons in the active sector receive this event
		Quadrant q = getActiveQuadrant();
		if (!getDisruptor().isBroken()) {
			repositionKlingon(q);
			fireOnEnterprise(q);
		} else
			flee(q);
	}

	public void destroy() {
		removeHandler(this);
		getActiveQuadrant().remove(this);
		message(getName() + " was destroyed", "klingon-destroyed");
		fireEvent(KLINGON_DESTROYED, (h) -> h.onVesselDestroyed(Klingon.this));
	}

	public void repair() {
		getImpulse().setBroken(false);
		getDisruptor().setBroken(false);
		getCloak().setBroken(false);
		getCloak().setValue(true);
		getShields()
				.setCurrentUpperBound(Math.max(getShields().getMaximum() * 0.5, getShields().getCurrentUpperBound()));
		getShields().setValue(getShields().getCurrentUpperBound());
	}

	@Override
	public void onActiveQuadrantChanged(Quadrant quadrantFrom, Quadrant quadrantTo) {
		if (quadrantFrom.contains(this)) {
			unregisterActionHandlers();
		}
		if (quadrantTo.contains(this)) {
			registerActionHandlers();
			repair();
			cloak.setValue(canCloak());
			css = "klingon " + (isVisible() ? "" : "cloaked");
			Location newLocation = getStarMap().findFreeSpot(quadrantTo);
			jumpTo(newLocation);
		}
	}

	void maybeDamage(Setting setting, double impact) {
		BrowserAPI random = getApplication().browserAPI;
		if (!setting.isBroken() && random.nextDouble() < impact)
			getImpulse().damageAndTurnOff(getStarMap().getStarDate());
	}
	
	@Override
	public void onFire(Quadrant quadrant, Vessel actor, Thing target, Weapon weapon, double damage, boolean wasAutoFire,
			partTarget part) {
		if (target != this)
			return;
		if (!isVisible()) {
			uncloak();
			destroy();
			return;
		}
		double impact = damage / (shields.getValue() + 1);
		shields.decrease(damage);
		BrowserAPI random = getApplication().browserAPI;
		shields.setCurrentUpperBound(shields.getCurrentUpperBound() - damage);
		if (part == partTarget.none) {
			maybeDamage(impulse, impact);
			maybeDamage(disruptor, impact);
			maybeDamage(cloak, impact);
		}
		message(weapon + " hit " + target.getName() + " at " + target.getLocation(), "klingon-damaged");
		if ((part != partTarget.none) && (random.nextDouble() < Constants.KLINGON_PRECISION_SHOT_CHANCE_DAMAGE)) {
			switch (part) {
			case weapons:
				disruptor.setBroken(true);
				message(weapon + " disabled " + target.getName() + " disruptors.", "klingon-damaged");
				break;
			case propulsion:
				impulse.setBroken(true);
				message(weapon + " disabled " + target.getName() + " propulsion.", "klingon-damaged");
				break;
			default:
			}
		}
		if (shields.getValue() <= 0)
			destroy();
	}

	@Override
	public boolean isVisible() {
		return !cloak.getBooleanValue();
	}

	public static boolean isCloakedKlingon(Thing thing) {
		return (Klingon.is(thing)) && !thing.isVisible();
	}

	public static boolean isEmptyOrCloakedKlingon(Thing thing) {
		return thing == null || isCloakedKlingon(thing);
	}

	public static boolean is(Thing thing) {
		return thing instanceof Klingon;
	}

	public static Klingon as(Thing thing) {
		return (Klingon) thing;
	}

	@Override
	public void beforeGameRestart() {
		//TODO: is this necessary? the game reloads
		removeHandler(this);
	}

	@Override
	public void onGameStarted(StarMap map) {
		if (getActiveQuadrant().contains(this))
			registerActionHandlers();
	}

	@Override
	public String getCss() {
		return css;
	}
	
	public ShipClass getShipClass() {
		return sc;
	}

}
