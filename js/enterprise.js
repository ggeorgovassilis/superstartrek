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
			Enterprise.fireAtWill=false;
			Enterprise.tacticalComputerOnline=true;
			$(window).trigger("setting_changed");
		},
		runComputer:function(){
			console.log("** running computer actions **");
			if (Enterprise.fireAtWill && Enterprise.tacticalComputerOnline)
				Enterprise.autoFire();
		},
		autoFire:function(){
			var klingons = Enterprise.quadrant.klingons;
			for (var i = 0; i<klingons.length;i++){
				var klingon = klingons[i];
				var distance = Tools.distance(Enterprise.x, Enterprise.y, klingon.x, klingon.y);
				if (distance<=Constants.PHASER_RANGE){
					console.log("Autofiring at",klingon);
					Enterprise.firePhasersAt(klingon.x, klingon.y);
					return;
				}
			}
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
		   if (Math.random()<impact && Enterprise.tacticalComputerOnline){
			   Enterprise.tacticalComputerOnline=false;
			   Enterprise.fireAtWill=false;
			   message+="<br>Tactical computer was damaged.";
			   $(window).trigger("settings_changed");
		   }
		   console.log(message);
		   $(window).trigger("enterprise_damaged");
		   $(window).trigger("settings_changed");
		   return IO.message(function() {}, message);
	   },
	   firePhasersAt:function(targetX, targetY){
			var distance = Tools.distance(Enterprise.x, Enterprise.y, targetX, targetY);
			if (Math.floor(distance)>Constants.PHASER_RANGE)
				return IO.message(Controller.showSectorSelectionMenu,
						"Target is out of range");
			var strength = Enterprise.phaserPower;
			var klingon = StarMap.getKlingonInQuadrantAt(Enterprise.quadrant,
					targetX, targetY);
			if (!klingon) {
				return IO.message(Controller.showSectorSelectionMenu,
						"No Klingon at that sector");
			}
			if (Math.floor(distance)>Constants.PHASER_RANGE)
				return IO.message(Controller.showSectorSelectionMenu,
						"Target is out of range");
			var consumption = Computer.calculateEnergyConsumptionForPhasers(strength);
			if (!Computer.hasEnergyBudgetFor(consumption))
				return IO.message("Insufficient energy");
			Computer.consume(consumption);
			var damage = strength
					/ Tools.distance(Enterprise.x, Enterprise.y, klingon.x, klingon.y);
			$window.trigger("fired");
			Klingons.damage(klingon, damage);
		}
};
