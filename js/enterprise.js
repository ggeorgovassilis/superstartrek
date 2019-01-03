/*
 * Enterprise
 */
var Enterprise={
		repairAtStarbase:function(){
			Enterprise.energy=Constants.MAX_ENERGY;
			Enterprise.budget=Constants.MAX_REACTOR_OUTPUT;
			Enterprise.maxShields=Constants.ENTERPRISE_MAX_SHIELDS; // maximum level of shields
			Enterprise.shields=Enterprise.maxShields; // current shield level. Enemy fire reduces them; they replenish at the beginning of a new round
			Enterprise.userDefinedShields=Enterprise.maxShields; // that's how much the player set shields. actual shields (see shields property above) might be lower.
			Enterprise.reactorOutput=Constants.MAX_REACTOR_OUTPUT;
			Enterprise.torpedos=Constants.MAX_TORPEDOS;
			Enterprise.maxImpulse=Constants.MAX_IMPULSE_SPEED;
			Enterprise.phaserPower=Constants.ENTERPRISE_MAX_PHASER_POWER;
			Enterprise.lrsOnline=true;
			Enterprise.torpedosOnline=true;
			Enterprise.fireAtWill=true;
			Enterprise.tacticalComputerOnline=true;
			Enterprise.maxWarpSpeed=Constants.ENTERPRISE_MAX_WARP_SPEED;
			Enterprise.isDamaged=false;
			Events.trigger(Events.SETTINGS_CHANGED);
		},
		repairProvisionally:function(){
			var message = "";
			var duration = Constants.DURATION_OF_PROVISIONAL_REPAIRS;
			var cost = duration * Computer.calculateBaseEnergyConsumption();
			if (Computer.consume(cost))
				return;
			Computer.advanceClock(duration);
			var message = null;
			function canFix(){
				return Math.random()<0.1;
			}
			for (var i=0;i<40;i++){//quit loop if nothing repaired after 40 turns
				if (Enterprise.maxImpulse<2 && canFix()){
					   Enterprise.maxImpulse++;
					   message="Improved impulse drive.";
					   break;
					}
				if (!Enterprise.lrsOnline && canFix()){
				    Enterprise.lrsOnline=true;
					message="Repaired long range scan.";
					break;
				}
				if (!Enterprise.tacticalComputerOnline && canFix()){
					   Enterprise.tacticalComputerOnline=true;
					   message="Repaired tactical computer.";
					   break;
					}
			   if (Enterprise.reactorOutput<0.6*Constants.MAX_REACTOR_OUTPUT){
					message = "Improved reactor output.";
					Enterprise.reactorOutput+=70;
					break;
			   }
				if (Enterprise.maxShields<0.60*Constants.ENTERPRISE_MAX_SHIELDS && canFix()){
					message = "Improved shields.";
					Enterprise.maxShields+=10;
					break;
				}
				if (Enterprise.phaserPower<0.60*Constants.ENTERPRISE_MAX_PHASER_POWER && canFix()){
					Enterprise.phaserPower = Enterprise.phaserPower+10;
					message="Improved phasers.";
					break;
				}
				if (!Enterprise.torpedosOnline && canFix()){
				   Enterprise.torpedosOnline=true;
				   message="Repaired torpedo bay.";
				   break;
				}
				if (Enterprise.maxWarpSpeed==1 && canFix()){
				   Enterprise.maxWarpSpeed++;
				   message="Improved warp drive.";
				   break;
				}
			}
			if (message)
				Events.trigger(Events.ENTERPRISE_REPAIRED);
			else message="Engineering couldn't repair anything at this time.";
				return IO.message(message);
		},
		runComputer:function(){
			if (Enterprise.fireAtWill && Enterprise.tacticalComputerOnline)
				Enterprise.autoFire();
		},
		autoFire:function(){
			var klingons = Enterprise.quadrant.klingons;
			for (var i = 0; i<klingons.length;i++){
				var klingon = klingons[i];
				var distance = Tools.distance(Enterprise.x, Enterprise.y, klingon.x, klingon.y);
				if (distance<=Constants.PHASER_RANGE){
					return Enterprise._firePhasersAt(klingon.x, klingon.y, true);
				}
			}
		},
		setup:function(){
			Enterprise.quadrant=StarMap.quadrants[0];
			Enterprise.x=0;
			Enterprise.y=0;
			Enterprise.repairAtStarbase();
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
	   assignDamage:function(damage,cause){
		   var impact = Math.pow(damage/(Enterprise.shields+1),2); //scale impact: low impact doesn't hurt us at all, high impact a lot
		   Enterprise.shields = Math.max(0,Enterprise.shields - damage);
		   Enterprise.maxShields = Math.max(0,Enterprise.maxShields-(Enterprise.maxShields*impact));
		   Enterprise.shields = Math.min(Enterprise.shields, Enterprise.maxShields);
		   if (Enterprise.shields == 0) {
				Events.trigger(Events.GAME_OVER,{message:"Enterprise was destroyed.", cause:cause.name});
				return;
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
			   Events.trigger(Events.SETTINGS_CHANGED);
		   }
		   if (Math.random()<impact && Enterprise.maxImpulse){
			   Enterprise.maxImpulse--;
			   message+="<br>Impulse drive was damaged.";
			   Events.trigger(Events.SETTINGS_CHANGED);
		   }
		   if (Math.random()<impact && Enterprise.maxWarpSpeed>1){
			   Enterprise.maxWarpSpeed--;
			   message+="<br>Warp drive was damaged.";
			   Events.trigger(Events.SETTINGS_CHANGED);
		   }
		   if (Math.random()<impact && Enterprise.reactorOutput>0){
			   Enterprise.reactorOutput-=(Constants.MAX_REACTOR_OUTPUT/20);
			   message+="<br>Reactor was damaged.";
			   Events.trigger(Events.SETTINGS_CHANGED);
			   if (Enterprise.reactorOutput<0){
					Events.trigger(Events.GAME_OVER,{message:"Enterprise was destroyed.", cause:cause.name});
					return;
			   }
		   }
		   Enterprise.isDamaged=true;
		   Events.trigger(Events.ENTERPRISE_DAMAGED);
		   return IO.message(message).then.nothing();
	   },
	   firePhasersAt:function(targetX, targetY){
		   var v = Enterprise._firePhasersAt(targetX, targetY, false);
		   if (v)
			   return v;
		   return IO.endTurn();
	   },
	   _firePhasersAt:function(targetX, targetY, autoaim){
			var distance = Tools.distance(Enterprise.x, Enterprise.y, targetX, targetY);
			if (Math.floor(distance)>Constants.PHASER_RANGE)
				return IO.message("Target is out of range").then.SRS();
			var strength = Enterprise.phaserPower;
			var klingon = StarMap.getKlingonInQuadrantAt(Enterprise.quadrant,
					targetX, targetY);
			if (!klingon) {
				return IO.message("No Klingon at that sector").then.SRS();
			}
			if (Math.floor(distance)>Constants.PHASER_RANGE)
				return IO.message("Target is out of range").then.SRS();
			var consumption = Computer.calculateEnergyConsumptionForPhasers(strength);
			if (!Computer.hasEnergyBudgetFor(consumption))
				return IO.message("Insufficient energy").then.SRS();
			var damage = strength
					/ Tools.distance(Enterprise.x, Enterprise.y, klingon.x, klingon.y);
			Events.trigger(Events.WEAPON_FIRED);
			if (autoaim)
				IO.message("Autoaim fires at "+klingon.name);
			Klingons.damage(klingon, damage);
			if (Computer.consume(consumption))
				return;
			IO.SRS();
	   }
};