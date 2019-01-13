/**
 * Constants.
 * Refer to wikipedia articles on stardate and warpspeed for why constants were assigned their current values.
 * Phasers lose power over distance in the original game. No information about disruptors.
 */

var nop = function(){};

var Constants = {
		DURATION_OF_MOVEMENT_PER_SECTOR: 0.05,
		DURATION_OF_MOVEMENT_PER_QUADRANT: 1,
		DURATION_OF_ROUND:1,
		DURATION_OF_REFUELING:2,
		DURATION_OF_REPAIRS:4,
		DURATION_OF_PROVISIONAL_REPAIRS:8,
		ENERGY_OF_MOVEMENT_PER_SECTOR: 20,
		ENERGY_PER_SHIELD:2,
		BASE_CONSUMPTION:10,
		ENERGY_OF_MOVEMENT_PER_QUADRANT_PER_WARP: function(speed){
			return 10+speed*speed*speed*2;
		},
		ENTERPRISE_MAX_SHIELDS:100,
		ENTERPRISE_MAX_WARP_SPEED:8,
		MAX_ENERGY:8000,
		MAX_TORPEDOS:10,
		MAX_TORPEDO_DAMAGE:50,
		MAX_IMPULSE_SPEED:3,
		MAX_WARP_SPEED:4,
		MAX_REACTOR_OUTPUT:290,
		PHASER_EFFICIENCY:1,
		ENTERPRISE_MAX_PHASER_POWER:45,
		PHASER_RANGE:3,
		SMALL_HEIGHT:450,
		SMALL_WIDTH:621,
		STARBASES_ON_MAP:3,
		
		CHANCE_OF_KLINGON_IN_QUADRANT:0.3,
		KLINGON_DISRUPTOR_POWER:10,
		MAX_KLINGON_SHIELD:100,
		KLINGON_IMPULSE_SPEED:1,//must be integer because of use in a*
		MAX_KLINGONS_IN_QUADRANT:5,
		KLINGON_SHIP_CLASS_MODIFIERS:[
			{name:"a Klingon raider", modifier:0.5, symbol:"c-}"},
			{name:"a Klingon Bird-of-Prey", modifier:1, symbol:"C-D"}
		],
		DISRUPTOR_RANGE:2.83, //allow up to 2 sectors diagonically
};


var CommandBar={
		element:$("#commandbar")
};

Events.on(Events.ENTERPRISE_DAMAGED, Computer.updateShieldsIndicator);
Events.on(Events.ENTERPRISE_DAMAGED, Computer.updateDamagedIndicator);
Events.on(Events.ENTERPRISE_REPAIRED, Computer.updateDamagedIndicator);
Events.on(Events.START_GAME, registerServiceWorker);
window.onbeforeunload = function(e){
		return "Are you sure you want to leave?"; 
};

