package superstartrek;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mockito.AdditionalMatchers;

import superstartrek.client.activities.combat.CombatHandler;
import superstartrek.client.activities.combat.CombatHandler.partTarget;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.activities.klingons.Klingon.ShipClass;
import superstartrek.client.activities.messages.MessageHandler;
import superstartrek.client.activities.navigation.NavigationHandler;
import superstartrek.client.bus.Events;
import superstartrek.client.control.QuadrantActivationHandler;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Star;
import superstartrek.client.model.StarBase;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;
import superstartrek.client.model.Weapon;
import superstartrek.client.model.Enterprise.ShieldDirection;
import superstartrek.client.model.Star.StarClass;
import superstartrek.client.utils.BrowserAPI;

import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;

public class TestEnterprise extends BaseTest {

	@Test
	public void test_damage_to_torpedos() {
		MessageHandler handler = mock(MessageHandler.class);
		bus.addHandler(Events.MESSAGE_POSTED, handler);

		enterprise.damageTorpedos();

		assertFalse(enterprise.getTorpedos().isOperational());
		verify(handler).messagePosted(eq("Torpedo bay damaged"), eq("enterprise-damaged"));
	}

	@Test
	public void test_damage_Phasers() {
		MessageHandler handler = mock(MessageHandler.class);
		bus.addHandler(Events.MESSAGE_POSTED, handler);

		enterprise.damagePhasers();
		assertTrue(enterprise.getPhasers().isOperational());
		assertEquals(21, enterprise.getPhasers().getCurrentUpperBound(), 0.1);
		enterprise.damagePhasers();
		assertTrue(enterprise.getPhasers().isOperational());
		assertEquals(12, enterprise.getPhasers().getCurrentUpperBound(), 0.1);
		enterprise.damagePhasers();
		assertTrue(enterprise.getPhasers().isOperational());
		assertEquals(3, enterprise.getPhasers().getCurrentUpperBound(), 0.1);
		enterprise.damagePhasers();
		assertEquals(0, enterprise.getPhasers().getCurrentUpperBound(), 0.1);
		assertFalse(enterprise.getPhasers().isOperational());
		verify(handler, times(4)).messagePosted(eq("Phaser banks damaged"), eq("enterprise-damaged"));
	}

	@Test
	public void test_damage_to_impulse() {
		MessageHandler handler = mock(MessageHandler.class);
		bus.addHandler(Events.MESSAGE_POSTED, handler);

		enterprise.damageImpulse();
		assertTrue(enterprise.getImpulse().isOperational());
		assertEquals(2, enterprise.getImpulse().getCurrentUpperBound(), 0.1);

		enterprise.damageImpulse();
		assertTrue(enterprise.getImpulse().isOperational());
		assertEquals(1, enterprise.getImpulse().getCurrentUpperBound(), 0.1);

		enterprise.damageImpulse();
		assertFalse(enterprise.getImpulse().isOperational());
		assertEquals(0, enterprise.getImpulse().getCurrentUpperBound(), 0.1);

		verify(handler, times(3)).messagePosted(eq("Impulse drive damaged"), eq("enterprise-damaged"));

	}

	@Test
	public void test_navigate_to() {
		Quadrant quadrant = new Quadrant("q 1 2", 1, 2);
		starMap.setQuadrant(quadrant);
		enterprise.setQuadrant(quadrant);
		enterprise.setLocation(Location.location(0, 0));
		bus.addHandler(Events.THING_MOVED, new NavigationHandler() {

			@Override
			public void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
				assertEquals(enterprise, thing);
				assertEquals(quadrant, qFrom);
				assertEquals(quadrant, qTo);
				assertEquals(Location.location(2, 2), lTo);
			}
		});
		// necessary call to findReachableSectors in order to populate reachability map
		enterprise.findReachableSectors();
		enterprise.navigateTo(Location.location(2, 2));

		assertEquals(0, enterprise.getImpulse().getValue(), 0.5);
		assertEquals(Location.location(2, 2), enterprise.getLocation());
		assertEquals(1, bus.getFiredCount(Events.THING_MOVED));
	}

	@Test
	public void test_warp_to() {
		Quadrant quadrant = new Quadrant("q 1 2", 1, 2);
		starMap.setQuadrant(quadrant);
		enterprise.setQuadrant(quadrant);
		enterprise.setLocation(Location.location(0, 0));
		application.browserAPI = mock(BrowserAPI.class);
		when(application.browserAPI.nextInt(any(int.class))).thenReturn(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);

		// path between source and target needs to exist for collision check
		starMap.setQuadrant(new Quadrant("_", 2, 3));

		// quadrants around target need to exist because exploration flag is set
		for (int x = 3 - 1; x <= 3 + 1; x++)
			for (int y = 4 - 1; y <= 4 + 1; y++)
				starMap.setQuadrant(new Quadrant("_", x, y));

		Quadrant targetQuadrant = starMap.getQuadrant(3, 4);
		starMap.setQuadrant(targetQuadrant);

		bus.addHandler(Events.QUADRANT_ACTIVATED, new QuadrantActivationHandler() {

			@Override
			public void onActiveQuadrantChanged(Quadrant oldQuadrant, Quadrant newQuadrant) {
				assertEquals(quadrant, oldQuadrant);
				assertEquals(targetQuadrant, newQuadrant);
			}
		});
		enterprise.warpTo(targetQuadrant, null);

		assertEquals(1, bus.getFiredCount(Events.QUADRANT_ACTIVATED));
	}

	@Test
	public void test_fire_Phasers() {
		application.browserAPI = mock(BrowserAPI.class);
		when(application.browserAPI.nextDouble()).thenReturn(0.5, 0.6, 0.1, 0.3, 0.3);
		Quadrant quadrant = new Quadrant("q 1 2", 1, 2);
		starMap.setQuadrant(quadrant);
		enterprise.setQuadrant(quadrant);

		Klingon klingon = new Klingon(ShipClass.BirdOfPrey);
		klingon.setLocation(Location.location(1, 1));
		quadrant.add(klingon);
		klingon.registerActionHandlers();
		klingon.uncloak();

		CombatHandler handler = mock(CombatHandler.class);

		bus.addHandler(Events.BEFORE_FIRE, handler);

		assertEquals(100, klingon.getShields().getValue(), 0.1);
		enterprise.firePhasersAt(klingon.getLocation(), false, partTarget.none);
		assertEquals(89, klingon.getShields().getValue(), 10);

		assertEquals(1, bus.getFiredCount(Events.BEFORE_FIRE));
		// TODO: damage probably wrong
		verify(handler, times(1)).onFire(same(quadrant), same(enterprise), same(klingon), eq(Weapon.phaser),
				AdditionalMatchers.eq(8.5, 1), eq(false), eq(partTarget.none));
	}

	@Test
	public void test_fire_phasers_precision_shot() {
		application.browserAPI = mock(BrowserAPI.class);
		when(application.browserAPI.nextDouble()).thenReturn(0.5, 0.6, 0.1, 0.3, 0.3);
		Quadrant quadrant = new Quadrant("q 1 2", 1, 2);
		starMap.setQuadrant(quadrant);
		enterprise.setQuadrant(quadrant);
		quadrant.add(enterprise);

		Klingon klingon = new Klingon(ShipClass.BirdOfPrey);
		klingon.setLocation(Location.location(1, 1));
		quadrant.add(klingon);
		klingon.registerActionHandlers();
		klingon.uncloak();

		CombatHandler handler = mock(CombatHandler.class);

		bus.addHandler(Events.BEFORE_FIRE, handler);

		assertEquals(100, klingon.getShields().getValue(), 0.1);
		enterprise.firePhasersAt(klingon.getLocation(), false, partTarget.propulsion);
		assertEquals(89, klingon.getShields().getValue(), 10);

		assertEquals(1, bus.getFiredCount(Events.BEFORE_FIRE));
		// TODO: damage probably wrong
		verify(handler, times(1)).onFire(same(quadrant), same(enterprise), same(klingon), eq(Weapon.phaser),
				AdditionalMatchers.eq(4, 1), eq(false), eq(partTarget.propulsion));
	}

	@Test
	public void test_FirePhasers_negative() {
		application.browserAPI = mock(BrowserAPI.class);
		when(application.browserAPI.nextDouble()).thenReturn(0.5, 0.6, 0.1, 0.3, 0.3);
		Quadrant quadrant = new Quadrant("q 1 2", 1, 2);
		starMap.setQuadrant(quadrant);
		enterprise.setQuadrant(quadrant);
		quadrant.add(enterprise);

		Klingon klingon = new Klingon(ShipClass.BirdOfPrey);
		klingon.setLocation(Location.location(1, 1));
		quadrant.add(klingon);
		klingon.registerActionHandlers();
		klingon.uncloak();

		CombatHandler handler = mock(CombatHandler.class);

		MessageHandler messageHandler = mock(MessageHandler.class);
		bus.addHandler(Events.BEFORE_FIRE, handler);
		bus.addHandler(Events.MESSAGE_POSTED, messageHandler);

		enterprise.getReactor().setValue(0);

		assertEquals(100, klingon.getShields().getValue(), 0.1);
		enterprise.firePhasersAt(klingon.getLocation(), false, partTarget.none);
		assertEquals(100, klingon.getShields().getValue(), 10);

		assertEquals(0, bus.getFiredCount(Events.BEFORE_FIRE));
		// TODO: damage probably wrong
		verify(handler, times(0)).onFire(same(quadrant), same(enterprise), same(klingon), eq(Weapon.phaser),
				AdditionalMatchers.eq(21, 1), eq(false), eq(partTarget.none));
		verify(messageHandler).messagePosted("Insufficient reactor output", "info");
	}

	@Test
	public void test_fire_torpedos() {

		application.browserAPI = mock(BrowserAPI.class);
		when(application.browserAPI.nextDouble()).thenReturn(0.5, 0.6, 0.1, 0.3, 0.3);

		Quadrant quadrant = new Quadrant("q 1 2", 1, 2);
		starMap.setQuadrant(quadrant);
		enterprise.setQuadrant(quadrant);

		Klingon klingon = new Klingon(ShipClass.BirdOfPrey);
		quadrant.add(klingon);
		klingon.setLocation(Location.location(1, 1));
		klingon.registerActionHandlers();
		klingon.uncloak();

		CombatHandler handler = mock(CombatHandler.class);

		bus.addHandler(Events.BEFORE_FIRE, handler);
		bus.addHandler(Events.AFTER_FIRE, handler);

		assertEquals(100, klingon.getShields().getValue(), 0.1);
		enterprise.fireTorpedosAt(klingon.getLocation());
		assertEquals(50, klingon.getShields().getValue(), 75);

		assertEquals(1, bus.getFiredCount(Events.AFTER_FIRE));

		verify(handler, times(1)).onFire(quadrant, enterprise, klingon, Weapon.torpedo, 25, false, partTarget.none);
		verify(handler, times(1)).afterFire(quadrant, enterprise, klingon, Weapon.torpedo, 25, false);

	}

	@Test
	public void test_getReachableSectors() {
		Quadrant quadrant = new Quadrant("q 1 2", 1, 2);
		starMap.setQuadrant(quadrant);
		enterprise.setQuadrant(quadrant);
		enterprise.setLocation(Location.location(4, 4));
		starMap.enterprise = enterprise;
		quadrant.add(new Star(1, 6, StarClass.A));
		quadrant.add(new Star(2, 6, StarClass.A));
		quadrant.add(new Star(3, 6, StarClass.A));
		quadrant.add(new Star(5, 6, StarClass.A));
		quadrant.add(new Star(6, 6, StarClass.A));
		quadrant.add(new Star(7, 6, StarClass.A));
		quadrant.add(new Star(4, 3, StarClass.A));
		List<Location> list = enterprise.findReachableSectors();
		assertTrue(list.contains(Location.location(4, 5)));
		assertTrue(list.contains(Location.location(4, 6)));
		assertTrue(list.contains(Location.location(5, 5)));
		assertTrue(list.contains(Location.location(3, 3)));
		assertTrue(list.contains(Location.location(3, 4)));
		assertTrue(list.contains(Location.location(5, 4)));
		assertFalse(list.contains(Location.location(5, 6)));
		assertFalse(list.contains(Location.location(3, 6)));
		// check for duplicates
		assertEquals(new HashSet<>(list).size(), list.size());
		assertEquals(19, list.size());
	}

	@Test
	public void test_isDamaged() {
		assertFalse(enterprise.isDamaged());
		enterprise.damagePhasers();
		assertTrue(enterprise.isDamaged());
	}

	@Test
	public void test_canFirePhaserAt() {
		Quadrant quadrant = new Quadrant("q 1 2", 1, 2);
		starMap.setQuadrant(quadrant);
		enterprise.setQuadrant(quadrant);
		enterprise.setLocation(Location.location(4, 4));
		quadrant.add(enterprise);

		Star star = new Star(3, 3, Star.StarClass.A);
		quadrant.add(star);
		Klingon klingon = new Klingon(Klingon.ShipClass.BirdOfPrey);
		klingon.setLocation(Location.location(5, 5));
		quadrant.add(klingon);
		assertEquals("There is nothing at 7:7", enterprise.canFirePhaserAt(Location.location(7, 7)));
		assertEquals("Phasers can target only enemy vessels", enterprise.canFirePhaserAt(star.getLocation()));

		klingon.getCloak().setValue(true);
		assertEquals("There is nothing at 5:5", enterprise.canFirePhaserAt(klingon.getLocation()));

		klingon.uncloak();
		assertNull(null, enterprise.canFirePhaserAt(klingon.getLocation()));
	}

	@Test
	public void test_autoAim() {

		Klingon klingon = new Klingon(Klingon.ShipClass.BirdOfPrey);
		klingon.setLocation(Location.location(5, 5));
		klingon.uncloak();
		enterprise.setLocation(Location.location(4, 4));
		quadrant.add(klingon);
		bus.addHandler(Events.BEFORE_FIRE, new CombatHandler() {

			@Override
			public void onFire(Quadrant quadrant, Vessel actor, Thing target, Weapon weapon, double damage,
					boolean wasAutoFire, partTarget part) {
				assertEquals(klingon, target);
				assertEquals(enterprise, actor);
				assertEquals(8.5, damage, 1);
				assertEquals(partTarget.none, part);
			}
		});
		bus.addHandler(Events.AFTER_FIRE, new CombatHandler() {

			@Override
			public void afterFire(Quadrant quadrant, Vessel actor, Thing target, Weapon weapon, double damage,
					boolean wasAutoFire) {
				assertEquals(klingon, target);
				assertEquals(enterprise, actor);
				assertEquals(8.5, damage, 1);
			}
		});
		enterprise.autoAim();
		assertEquals(1, bus.getFiredCount(Events.BEFORE_FIRE));
		assertEquals(1, bus.getFiredCount(Events.AFTER_FIRE));
	}

	@Test
	public void test_dock_with_starbase() {
		enterprise.setLocation(Location.location(1, 1));
		enterprise.getPhasers().damage(10, starMap.getStarDate());
		enterprise.getAntimatter().decrease(10);
		enterprise.getTorpedos().damage(1, starMap.getStarDate());
		enterprise.getImpulse().damage(1, starMap.getStarDate());
		quadrant.setStarBase(new StarBase(Location.location(3, 3)));
		enterprise.getEvasiveManeuvers().setValue(true);

		bus.addHandler(Events.THING_MOVED, new NavigationHandler() {

			@Override
			public void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
				assertEquals(enterprise, thing);
				assertEquals(quadrant, qFrom);
				assertEquals(Location.location(1, 1), lFrom);
				assertEquals(quadrant, qTo);
				assertEquals(Location.location(4, 4), lTo);
			}
		});
		when(browser.nextDouble()).thenReturn(0.0);
		when(browser.nextInt(any(int.class))).thenReturn(1, 1, 2, 2, 5);
		enterprise.dockInStarbase();

		assertEquals(1, bus.getFiredCount(Events.THING_MOVED));
		assertEquals(Location.location(4, 4), enterprise.getLocation());

		assertEquals(1, bus.getFiredCount(Events.ENTERPRISE_REPAIRED));
		assertEquals(enterprise.getTorpedos().getMaximum(), enterprise.getTorpedos().getValue(), 0.1);
		assertFalse(enterprise.getEvasiveManeuvers().getBooleanValue());
	}

	@Test
	public void test_applyDamage() {
		assertFalse(enterprise.getEvasiveManeuvers().getBooleanValue());
		when(browser.nextDouble()).thenReturn(1.0);
		assertEquals(60, enterprise.getShields().getValue(), 0.1);
		enterprise.applyDamage(30);
		assertEquals(50.1, enterprise.getShields().getValue(), 0.1);
	}

	@Test
	public void test_applyDamage_with_evasive_maneuvers() {
		enterprise.getEvasiveManeuvers().setValue(true);
		assertTrue(enterprise.getEvasiveManeuvers().getBooleanValue());
		when(browser.nextDouble()).thenReturn(1.0);
		assertEquals(60, enterprise.getShields().getValue(), 0.1);
		enterprise.applyDamage(30);
		assertEquals(53.1, enterprise.getShields().getValue(), 0.1);
	}

	@Test
	public void test_onFire_no_directional_shield() {
		when(browser.nextDouble()).thenReturn(1.0);
		Klingon klingon = new Klingon(ShipClass.BirdOfPrey);
		quadrant.add(klingon);
		klingon.setLocation(Location.location(1, 1));
		klingon.registerActionHandlers();
		klingon.uncloak();
		enterprise.onFire(quadrant, klingon, enterprise, Weapon.disruptor, klingon.getDisruptor().getValue(), false,
				partTarget.none);
		assertEquals(56, enterprise.getShields().getValue(), 0.1);
	}

	@Test
	public void test_onFire_with_directional_shield_side_fire() {
		assertEquals(enterprise.getLocation(), Location.location(0, 0));
		when(browser.nextDouble()).thenReturn(1.0);
		Klingon klingon = new Klingon(ShipClass.BirdOfPrey);
		klingon.setLocation(Location.location(1, 0));
		quadrant.add(klingon);
		klingon.registerActionHandlers();
		klingon.uncloak();
		enterprise.setShieldDirection(ShieldDirection.north);
		enterprise.onFire(quadrant, klingon, enterprise, Weapon.disruptor, klingon.getDisruptor().getValue(), false,
				partTarget.none);
		assertEquals(55.4, enterprise.getShields().getValue(), 0.1);
	}

	@Test
	public void test_onFire_with_directional_shield_rear_fire() {
		assertEquals(enterprise.getLocation(), Location.location(0, 0));
		when(browser.nextDouble()).thenReturn(1.0);
		Klingon klingon = new Klingon(ShipClass.BirdOfPrey);
		klingon.setLocation(Location.location(0, 1));
		quadrant.add(klingon);
		klingon.registerActionHandlers();
		klingon.uncloak();
		enterprise.setShieldDirection(ShieldDirection.north);
		enterprise.onFire(quadrant, klingon, enterprise, Weapon.disruptor, klingon.getDisruptor().getValue(), false,
				partTarget.none);
		assertEquals(54.1, enterprise.getShields().getValue(), 0.1);
	}

	@Test
	public void test_onFire_with_directional_shield_front_fire() {
		enterprise.setLocation(Location.location(0, 2));
		assertEquals(enterprise.getLocation(), Location.location(0, 2));
		when(browser.nextDouble()).thenReturn(1.0);
		Klingon klingon = new Klingon(ShipClass.BirdOfPrey);
		klingon.setLocation(Location.location(0, 1));
		quadrant.add(klingon);
		klingon.registerActionHandlers();
		klingon.uncloak();
		enterprise.setShieldDirection(ShieldDirection.north);
		enterprise.onFire(quadrant, klingon, enterprise, Weapon.disruptor, klingon.getDisruptor().getValue(), false,
				partTarget.none);
		assertEquals(56.75, enterprise.getShields().getValue(), 0.1);
	}

	@Test
	public void test_computeDirectionalShieldEfficiency() {
		enterprise.setLocation(Location.location(1, 1));
		assertEquals(0.75, enterprise.computeDirectionalShieldEfficiency(ShieldDirection.omni, Location.location(1, 0)),
				0.1);
		assertEquals(0.75, enterprise.computeDirectionalShieldEfficiency(ShieldDirection.omni, Location.location(1, 0)),
				0.1);

		assertEquals(1.0, enterprise.computeDirectionalShieldEfficiency(ShieldDirection.north, Location.location(1, 0)),
				0.1);
		assertEquals(0.0, enterprise.computeDirectionalShieldEfficiency(ShieldDirection.south, Location.location(1, 0)),
				0.1);
		assertEquals(0.5, enterprise.computeDirectionalShieldEfficiency(ShieldDirection.west, Location.location(1, 0)),
				0.1);

		assertEquals(1.0, enterprise.computeDirectionalShieldEfficiency(ShieldDirection.west, Location.location(0, 1)),
				0.1);
	}

	@Test
	public void test_toggleShields_no_klingons() {
		assertEquals(ShieldDirection.omni, enterprise.getShieldDirection());
		enterprise.toggleShields();
		//1st toggle in turn returns best direction
		assertEquals(ShieldDirection.omni, enterprise.getShieldDirection());
		enterprise.toggleShields();
		assertEquals(ShieldDirection.north, enterprise.getShieldDirection());
		enterprise.toggleShields();
		assertEquals(ShieldDirection.east, enterprise.getShieldDirection());
		enterprise.toggleShields();
		assertEquals(ShieldDirection.south, enterprise.getShieldDirection());
		enterprise.toggleShields();
		assertEquals(ShieldDirection.west, enterprise.getShieldDirection());
		enterprise.toggleShields();
		assertEquals(ShieldDirection.omni, enterprise.getShieldDirection());
	}

	@Test
	public void test_toggleShields_with_klingons() {
		Klingon k = new Klingon(ShipClass.BirdOfPrey);
		k.uncloak();
		k.setLocation(Location.location(1, 0));
		quadrant.add(k);
		assertEquals(ShieldDirection.omni, enterprise.getShieldDirection());
		enterprise.toggleShields();
		assertEquals(ShieldDirection.east, enterprise.getShieldDirection());
		enterprise.toggleShields();
		assertEquals(ShieldDirection.south, enterprise.getShieldDirection());
		enterprise.toggleShields();
		assertEquals(ShieldDirection.west, enterprise.getShieldDirection());
		enterprise.toggleShields();
		assertEquals(ShieldDirection.omni, enterprise.getShieldDirection());
	}

	@Test
	public void test_toggleShields_with_klingons2() {
		quadrant.remove(enterprise);
		enterprise.setLocation(Location.location(3, 3));
		quadrant.add(enterprise);
		
		Klingon k = new Klingon(ShipClass.BirdOfPrey);
		k.uncloak();
		k.setLocation(Location.location(2, 4));
		quadrant.add(k);
		assertEquals(ShieldDirection.omni, enterprise.getShieldDirection());
		enterprise.toggleShields();
		//1st toggle in turn returns best direction; 2nd will rotate shield around
		assertEquals(ShieldDirection.omni, enterprise.getShieldDirection());
		enterprise.toggleShields();
		assertEquals(ShieldDirection.north, enterprise.getShieldDirection());
		enterprise.toggleShields();
		assertEquals(ShieldDirection.east, enterprise.getShieldDirection());
	}
	
	@Test
	public void test_energy_consumption() {
		assertEquals(50.0, enterprise.getReactor().getValue(), 1.0);
		assertEquals(4.88, enterprise.computeEnergyConsumption(), 1.0);
		enterprise.getEvasiveManeuvers().setValue(true);
		assertEquals(5.8, enterprise.computeEnergyConsumption(), 1.0);
	}
}
