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
			Enterprise.phasersFiredThisTurn=false;
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
				return Math.random()<0.3;
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
			var klingon = klingons.filter(function(klingon){
				var distance = Tools.distance(Enterprise.x, Enterprise.y, klingon.x, klingon.y);
				if (!klingon.cloaked && distance<=Constants.PHASER_RANGE)
					return klingon;
			}).random();
			if (klingon)
				return Enterprise._firePhasersAt(klingon.x, klingon.y, true);
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
		   console.log("---------------------");
		   console.log(cause,"assigns damage",damage);
		   console.log("Shields initially at",Enterprise.shields);
		   var impact = Math.pow(((damage+1)/(Enterprise.shields+1)),1.1); //scale impact: low impact doesn't hurt us at all, high impact a lot
		   console.log("Impact",impact);
		   if (impact<=0)
			   return;
		   
		   Enterprise.shields = Math.max(0,Enterprise.shields - damage);
		   Enterprise.maxShields = Math.floor(Math.max(0,Enterprise.maxShields-(Enterprise.maxShields*impact)));
		   Enterprise.shields = Math.min(Enterprise.shields,Enterprise.maxShields);
		   console.log("Shields",Enterprise.shields,"max shields",Enterprise.maxShields);
		   var message = cause.name+" fired at us, shields dropped to "+Math.round(Enterprise.shields);
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
					Events.trigger(Events.GAME_OVER,{message:message, cause:cause.name});
					return;
			   }
		   }
		   if (Enterprise.shields < 1) {
			    message+="<br>Enterprise was destroyed."
				Events.trigger(Events.GAME_OVER,{message:message, cause:cause.name});
				return;
		   }
		   Enterprise.isDamaged=true;
		   Events.trigger(Events.ENTERPRISE_DAMAGED);
		   return IO.message(message).then.nothing();
	   },
	   resetForNewTurn:function(){
			Enterprise.budget=Enterprise.reactorOutput;
			Enterprise.shields = Enterprise.userDefinedShields;
			Enterprise.shields = Math.min(Enterprise.shields,Enterprise.maxShields);
	   },
	   fireTorpedosAt:function(targetX,targetY){
			var obstacle = Tools.findObstruction(Enterprise.quadrant, Enterprise.x,
					Enterprise.y, targetX, targetY);
			Enterprise.torpedos--;
			Events.trigger(Events.WEAPON_FIRED,{weapon:"torpedo"});
			if (obstacle) {
				var thing = obstacle.obstacle;
				if (thing.star) {
					return IO.message("Photon torpedo hit star at "
							+ thing.x + "," + thing.y).then.endTurn();
				}
				if (thing.starbase) {
					Enterprise.quadrant.starbases.remove(thing);
					return IO.message("Photon torpedo hit starbase at "
							+ thing.x + "," + thing.y).then.endTurn();
				}
				if (thing.klingon) {
					var klingon = thing;
					var chance = 1 / Math.log(1 + Tools.distance(Enterprise.x,
							Enterprise.y, klingon.x, klingon.y));
					if (Math.random() <= chance) {
						//photon torpedos are inefficient against shields; damage malus for full shields
						var damage = Constants.MAX_TORPEDO_DAMAGE*(1-0.9*(Math.sqr(klingon.shields/klingon.maxShields)));
						Klingons.damage(klingon, damage);
						return IO.endTurn();
					} else{
						IO.message("Photon torpedo missed target").then.endTurn();
					}
				}
			} else
				return IO.message("Photon torpedo exploded in the void.").then.endTurn();
	   },
	   firePhasersAt:function(targetX, targetY){
		   var v = Enterprise._firePhasersAt(targetX, targetY, false);
		   if (v)
			   return v;
		   return IO.endTurn();
	   },
	   _firePhasersAt:function(targetX, targetY, autoaim){
		    if (Enterprise.phasersFiredThisTurn)
				return IO.message("Phasers must be recharged before firing again.").then.SRS();
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
			Enterprise.phasersFiredThisTurn = true;
			var damage = strength
					/ Tools.distance(Enterprise.x, Enterprise.y, klingon.x, klingon.y);
			Events.trigger(Events.WEAPON_FIRED);
			if (autoaim)
				IO.message("Autoaim fires at "+klingon.name);
			Klingons.damage(klingon, damage);
			if (Computer.consume(consumption))
				return;
			IO.SRS();
	   },
	   toggleShields:function(){
			var maxShields = Enterprise.maxShields;
			var shields = Enterprise.userDefinedShields;
			if (Math.floor(shields) == Math.floor(maxShields))
				shields = 0;
			else shields += 25;
			shields = Math.min(shields, maxShields);
			Enterprise.userDefinedShields = shields;
			Enterprise.shields = shields;
			Events.trigger(Events.SETTINGS_CHANGED);
			Controller.showStartScreen();
	   },
	   rechargeForNewTurn:function(){
		   Enterprise.phasersFiredThisTurn = false;
	   }
};

Events.on(Events.TURN_STARTS,Enterprise.rechargeForNewTurn);