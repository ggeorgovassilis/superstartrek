package superstartrek.client.space;

public class Constants {

	public final static int ANIMATION_DURATION_MS=200;
	public final static double ANTIMATTER_WARNING_THRESHOLD = 0.18;
	public final static int POST_ANIMATION_DURATION_MS=ANIMATION_DURATION_MS+16;
	public final static int SECTORS_EDGE=8;
	final static int MIN_STARS_IN_QUADRANT = 4; 
	final static int MAX_STARS_IN_QUADRANT = 12; 
	final static int MAX_KLINGONS_IN_QUADRANT = 5; 
	final static double CHANCE_OF_KLINGONS_IN_QUADRANT = 0.2; 
	final static int NO_OF_STARBASES_ON_MAP = 3;
	public final static double ENTERPRISE_DOCKING_RANGE = 1.1;
	public final static int ENTERPRISE_DOCKING_VACANCY_RADIUS=2;
	public final static double ENTERPRISE_IMPULSE_CONSUMPTION = 2;
	public final static double ENTERPRISE_EVASIVE_MANEUVERS_DAMAGE_MODIFIER = 0.7;
	public final static double ENTERPRISE_DAMAGE_TORPEDO_LOSS_RATIO = 0.5;
	public final static double ENTERPRISE_DAMAGE_CONSTANT_IN_AFTER_WARP_GRACE_PERIOD = 0.5;
	public final static double ENTERPRISE_PHASER_RANGE = 3;
	public final static double ENTERPRISE_ANTIMATTER_CONSUMPTION_WARP = 2;
	public final static double ENTERPRISE_SHIELD_CONSUMPTION_FACTOR = 0.08;
	public final static double ENTERPRISE_DEVICE_IMPACT_MODIFIER = 0.3;
	public final static double ENTERPRISE_SHIELD_IMPACT_MODIFIER = 40.0;
	public final static double ENTERPRISE_SELF_REPAIR_STRENGTH= 0.75;
	public final static double ENTERPRISE_SHIELD_BASE_COEFFICIENT = 0.75;
	public final static double ENTERPRISE_SHIELD_DAMAGE_CHANCE = 0.7;
	public final static int ENTERPRISE_TIME_TO_REPAIR_SETTING = 3;
	public final static double ENTERPRISE_PRECISION_SHOT_EFFICIENCY = 0.2;
	public final static double ENTERPRISE_PHASER_EFFICIENCY = 0.4;
	public final static double ENTERPRISE_SHIELD_DIRECTIONAL_COEFFICIENT = 0.4;
	public final static double ENTERPRISE_SHIELD_BASE_DIRECTIONAL_IMPACT= 0.5;
	public final static int ENTERPRISE_PHASER_CAPACITY = 30;
	public final static int ENTERPRISE_TORPEDO_COUNT = 10;
	public final static int ENTERPRISE_ANTIMATTER = 1000;
	public final static int ENTERPRISE_REACTOR_CAPACITY = 50;
	public final static int ENTERPRISE_IMPULSE=3;
	public final static int ENTERPRISE_SHIELDS=60;
	public final static int START_DATE = 2100;
	public final static double ENTERPRISE_MIN_WARP_CONSUMPTION = 5;
	public final static double RED_ALERT_DISTANCE=3;
	public final static int KLINGON_MAX_SECTOR_SPEED = 1;
	public final static int KLINGON_DISRUPTOR_RANGE_SECTORS = 2;
	public final static double KLINGON_PRECISION_SHOT_CHANCE_DAMAGE = 0.5;
	public final static int MAX_HIGH_SCORE_ENTRIES = 5;
	public final static double KLINGON_SELF_REPAIR_FACTOR = 0.5;
	

	
	public static double ENTERPRISE_PHASER_PRECISION(boolean autoAim) {
		return 2.0 * (autoAim ? 1 : 0.7);	
	}
	
	public static double ENTERPRISE_TORPEDO_DAMAGE_ON_KLINGONS(double damage, double shields, double maxShields) {
		return damage * (1.0 - (0.5 * (shields / maxShields) * (shields / maxShields)));
	}
	
	public static double ENTERPRISE_APPLY_SHIELD_DAMAGE(double damage, double shieldValue) {
		return ENTERPRISE_SHIELD_IMPACT_MODIFIER  * damage / (shieldValue + 1.0);
	}
	
	public static double ENTERPRISE_APPLY_DEVICE_DAMAGE(double damage, double shieldValue) {
		return ENTERPRISE_DEVICE_IMPACT_MODIFIER * damage / (shieldValue + 1.0);
	}
}
