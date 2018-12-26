/*
 * Enterprise
 */
var Enterprise={
		repair:function(){
			Enterprise.energy=Constants.MAX_ENERGY;
			Enterprise.budget=Constants.MAX_REACTOR_OUTPUT;
			Enterprise.shields=0; // current shield level. Enemy fire reduces them; they replenish at the beginning of a new round
			Enterprise.maxShields=Constants.ENTERPRISE_MAX_SHIELDS; // maximum level of shields
			Enterprise.userDefinedShields=0; // that's how much the player set shields. actual shields (see shields property above) might be lower.
			Enterprise.reactorOutput=Constants.MAX_REACTOR_OUTPUT;
			Enterprise.torpedos=Constants.MAX_TORPEDOS;
			Enterprise.maxImpulse=Constants.MAX_IMPULSE_SPEED;
			Enterprise.phaserPower=Constants.ENTERPRISE_MAX_PHASER_POWER;
			Enterprise.lrsOnline=true;
			Enterprise.torpedosOnline=true;
		},
		setup:function(){
			Enterprise.quadrant=StarMap.quadrants[0];
			Enterprise.x=0;
			Enterprise.y=0;
			Enterprise.repair();
		},
	   repositionIfSectorOccupied:function(){
		   var newX = Enterprise.x;
		   var newY = Enterprise.y;
		   while (StarMap.getAnythingInQuadrantAt(Enterprise.quadrant, newX, newY)){
			   newX = Math.min(7,Math.max(0,Math.round(newX + 1-2*Math.random())));
			   newY = Math.min(7,Math.max(0,Math.round(newY + 1-2*Math.random())));
		   }
		   Enterprise.x = newX;
		   Enterprise.y = newY;
	   },
	   assignDamage:function(damage){
		   var impact = damage/Enterprise.shields;
		   Enterprise.shields = Math.max(0,Enterprise.shields - damage);
		   Enterprise.maxShields = Math.max(0,Enterprise.maxShields-(Enterprise.maxShields*impact));
		   Enterprise.shields = Math.min(Enterprise.shields, Enterprise.maxShields);
		   if (Enterprise.shields == 0) {
			   return IO.gameOverMessage("Klingon ship destroyed us, game over.");
		   }
		   var message = "Klingon ship fired at us, shields dropped to "+Math.round(Enterprise.shields);
		   if (Math.random()<impact){
			   Enterprise.phaserPower = Enterprise.phaserPower/2;
			   message+="<br>Phasers were damaged.";
		   }
		   if (Math.random()<impact && Enterprise.lrsOnline){
			   Enterprise.lrsOnline=false;
			   message+="<br>LRS was damaged.";
		   }
		   if (Math.random()<impact && Enterprise.torpedosOnline){
			   Enterprise.torpedosOnline=false;
			   message+="<br>Torpedo bay was damaged.";
		   }
		   console.log(message);
		   $(window).trigger("enterprise_damaged");
		   return IO.message(function() {}, message);
	   }
};
