/**
 * Constants.
 * Refer to wikipedia articles on stardate and warpspeed for why constants were assigned their current values.
 * Phasers lose power over distance in the original game. No information about disruptors.
 */

var nop = function(){};

var CommandBar={
		element:$("#commandbar")
};

Events.on(Events.ENTERPRISE_DAMAGED, Computer.updateShieldsIndicator);
Events.on(Events.ENTERPRISE_DAMAGED, Computer.updateDamagedIndicator);
Events.on(Events.ENTERPRISE_REPAIRED, Computer.updateDamagedIndicator);
Events.on(Events.START_GAME, Setup.registerServiceWorker);
window.onbeforeunload = function(e){
		return "Are you sure you want to leave?"; 
};
