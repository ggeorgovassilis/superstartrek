var Computer={
		element:$("#computerscreen"),
		updateEnergyIndicator:function(){
			var progress = 100*Enterprise.energy/Constants.MAX_ENERGY;
			var e = $("#cmd_showStatusReport");
			var indicator = e.find(".progress-indicator");
			if (progress<30)
				e.addClass("critical");
			else e.removeClass("critical");
			indicator.css("width",progress+"%");
		},
		updateDamagedIndicator:function(){
			if (Enterprise.isDamaged)
				Tools.addPageCss("enterprise-damaged");
			else
				Tools.removePageCss("enterprise-damaged");
		},
		updateShieldsIndicator:function(){
			$("#toggleShields .progress-indicator").css("width",Enterprise.shields+"%");
			$("#toggleShields .max-indicator").css("width",Enterprise.maxShields+"%");
		},
		updateStarbaseDockCommand:function(){
			var starbaseNearby = StarMap.isStarbaseAdjacent(Enterprise.quadrant, Enterprise.x, Enterprise.y);
			var isStarbaseNearby = !!starbaseNearby;
			if (isStarbaseNearby)
				Tools.addPageCss("starbase-nearby");
			else
				Tools.removePageCss("starbase-nearby");
		},
		advanceClock:function(duration){
			Computer.stardate+=duration;
		},
		updateStardate:function(){
			var stardateFormatted = Tools.formatStardate(Computer.stardate);
			Tools.setElementText($("#stardate"),stardateFormatted + " "+Enterprise.budget);
		},
		show:function(){
			Tools.defer("Computer_show",function(){
				Tools.gotoScreen("computer");
				Computer.updateStarbaseDockCommand();
				ShortRangeScanScreen.updateQuadrant(Enterprise.quadrant);
			});
		},
		calculateBaseEnergyConsumption:function(){
			return Enterprise.shields*Constants.ENERGY_PER_SHIELD 
			+ Constants.BASE_CONSUMPTION
			+ (Enterprise.isDamaged?Constants.BASE_CONSUMPTION:0)
			+ (Enterprise.tacticalComputerOnline?1:0)
			+ (Enterprise.fireAtWill?1:0)
			+ (Enterprise.torpedosOnline?1:0)
			+ (Enterprise.lrsOnline?1:0);
		},
		calculateEnergyConsumptionForMovement:function(distance){
			return distance*Constants.ENERGY_OF_MOVEMENT_PER_SECTOR;
		},
		calculateEnergyConsumptionForWarpDrive:function(quadrantFrom, quadrantTo){
			var distance = Tools.distance(quadrantFrom.x,quadrantFrom.y,quadrantTo.x,quadrantTo.y);
			//Reason for this strange formula: according to the wikipedia article on WS, WS is a cubic scale
			//We assume that the warp drive runs always at maximum speed
			var speed = Math.max(Constants.MAX_WARP_SPEED, distance);
			return Constants.ENERGY_OF_MOVEMENT_PER_QUADRANT_PER_WARP(speed)*distance/speed;
		},
		calculateEnergyConsumptionForPhasers:function(strength){
			return strength*Constants.PHASER_EFFICIENCY;
		},
		hasEnergyBudgetFor: function(amount){
			return Enterprise.budget >= amount;
		},
		consume:function(energy){
			Enterprise.budget=Math.max(0,Enterprise.budget-energy);
			Enterprise.energy-=energy;
			Enterprise.energy=Math.floor(Enterprise.energy);
			if (Enterprise.energy<=0){
				IO.call(Controller.gameOver);
			    IO.message("We run out of anti matter.","gameover");
				Events.trigger(Events.GAME_OVER,{message:"Game over",cause:"Enterprise"});
				return true;
			}
			Events.trigger(Events.ENTERPRISE_ENERGY_CHANGED);
		}
};

Events.on(Events.ENTERPRISE_ENERGY_CHANGED,	Computer.updateEnergyIndicator);
Events.on(Events.SETTINGS_CHANGED, Computer.updateShieldsIndicator);
Events.on(Events.ENTERPRISE_DAMAGED, Computer.updateShieldsIndicator);
Events.on(Events.ENTERPRISE_REPAIRED, Computer.updateShieldsIndicator);
