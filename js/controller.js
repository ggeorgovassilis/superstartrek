/**
 * Controller
 */
var Controller={
		sector:{x:0,y:0},
		currentToken:null,
		onClickedActivityToken:function(token){
			console.log("onClickedActivityToken",token);
			if (token == Controller.currentToken)
				return;
			if (token.startsWith("cmd_"))
				token = token.substring(4);
			var method = "";
			var arg1=undefined;
			var arg2=undefined;
			if (/\w+_\d_\d/.test(token)){
				var parts = /(\w+)_(\d)_(\d)/.exec(token);
				method = parts[1];
				arg1 = parseInt(parts[2]);
				arg2 = parseInt(parts[3]);
			} else
			if (/(\w+)_(\d+)/.test(token)){
				var parts = /(\w+)_(\d+)/.exec(token);
				method = parts[1];
				arg1 = parseInt(parts[2]);
			} else
				method = token;
			Computer.updateStardate();
			Tools.showScreen(method);
			Controller.currentHistoryToken = method;
			try{
				(Controller[method])(arg1, arg2);
			}
			catch(ex){
				console.log("error for method "+method);
				console.log(ex);
				throw ex;
			}
		},
		refuelAtStarbase:function(){
			Enterprise.energy = Constants.MAX_ENERGY;
			Enterprise.torpedos = Constants.MAX_TORPEDOS;
			Enterprise.shields = 0;
			Computer.advanceClock(Constants.DURATION_OF_REFUELING);
			Controller.endTurn();
		},
		repairAtStarbase:function(){
			Computer.advanceClock(Constants.DURATION_OF_REPAIRS);
			Enterprise.repairAtStarbase();
			Controller.refuelAtStarbase();
			Events.trigger(Events.ENTERPRISE_REPAIRED);
		},
		repairProvisionally:function(){
			Enterprise.repairProvisionally()
			return IO.endTurn();
		},
		showSectorSelectionMenu:function(s){
			Controller.sector.x = s.x;
			Controller.sector.y = s.y;
			$("#cmd_fireTorpedos").text("Photon torpedos ("+Enterprise.torpedos+")");
			Tools.addPageCss("sector-selected");
		},
		toggleShields:function(){
			var shields = Enterprise.userDefinedShields;
			var delta = -shields;
			if (shields >= Enterprise.maxShields)
				shields = 0;
			else
				shields=Math.max(0,Math.min(Enterprise.budget,Math.min(shields+25, Enterprise.maxShields)));
			delta+=shields;
			Computer.consume(delta);
			Enterprise.userDefinedShields = shields;
			Enterprise.shields = shields;
			Computer.updateShieldsIndicator();
			Controller.showStartScreen();
		},
		longrangescan:function(){
			console.log("show lrs")
			LongRangeScanScreen.show();
		},
		selectQuadrant:function(x,y){
			var quadrant = StarMap.getQuadrantAt(x, y);
			Controller.warpTo(quadrant);
		},
		computer:function(){
			Controller.showComputerScreen();
		},
		showComputerScreen:function(){
			Computer.advanceClock(0);
			Tools.removePageCss("sector-selected");
			Computer.show();
		},
		statusreport:function(){
			StatusReport.update();
		},
		showStartScreen:function(){
			Controller.showComputerScreen();
		},
		decorateUI:function(){
			$("button").each(function(i,e){
				e = $(e);
				var command = e.attr("command");
				if (command)
					e.on("click",Controller[command]);
			});
			$(".screen").each(function(i,e){
				e = $(e);
				var id = e.attr("id");
				Tools.addCssRule("."+id+" #"+id+"{display:block;}")
			});
		},
		startGame:function(){
			window.location.hash="#";
			console.log("Controller.startGame 1");
			Controller.decorateUI();
			Computer.stardate=2550;
			StarMap.constructQuadrants();
			Enterprise.setup();
			Enterprise.repositionIfSectorOccupied();
			Events.trigger(Events.START_GAME);
			Controller.showIntroScreen();
		},
		leaveIntro:function(){
			Intro.hide();
			Controller.showStartScreen();
		},
		cancel:function(){
			Controller.showComputerScreen();
		},
		showIntroScreen:function(){
			Intro.show();
			Tools.centerScreen();
		},
		nop:function(){
			Controller.endTurn();
		},
		newTurn:function(){
			Enterprise.budget=Enterprise.reactorOutput;
			Enterprise.shields = Enterprise.userDefinedShields;
			var consumption = Computer.calculateBaseEnergyConsumption();
			Computer.consume(consumption);
			Enterprise.shields = Math.min(Enterprise.shields,Enterprise.maxShields);
			if (!IO.isMessageShown())
				Controller.showComputerScreen();
			else IO.call(Controller.showComputerScreen);
			Enterprise.runComputer();
			Controller.showStartScreen();
		},
		toggleFireAtWill:function(){
			Enterprise.fireAtWill = !Enterprise.fireAtWill;
			Events.trigger(Events.SETTINGS_CHANGED);
		},
		updateFireAtWillButton:function(){
			Tools.removePageCss("fireAtWill");
			if (Enterprise.fireAtWill)
				Tools.addPageCss("fireAtWill");
		},
		endTurn:function(){
			if (StarMap.countKlingons()===0){
				Events.trigger(Events.GAME_OVER,{message:"All Klingons were eliminated. Congratulations!",cause:Enterprise});
				return;
			}
			Controller.showComputerScreen();
			Computer.advanceClock(Constants.DURATION_OF_ROUND);
			for (var qi=0;qi<StarMap.quadrants.length;qi++){
				var quadrant = StarMap.quadrants[qi];
				for (var ki=0;ki<quadrant.klingons.length;ki++){
					var klingon = quadrant.klingons[ki];
					Klingons.play(klingon, quadrant);
				}
			}
			if (IO.isMessageShown())
				IO.call(Controller.newTurn);
			else
				Controller.newTurn();
		},
		gameOver:function(e){ // Because of a bug, newTurn is called after gameOver, overwriting IO.callback.
			//This is why gameOver is called multiple times. We want to reload page on the last.
			console.log("***GAME OVER");
			Controller.newTurn=Controller.gameOver;
			if (e) IO.message(e.message);
			else
				window.setTimeout(function(){
					window.location.hash="";
					window.location.reload();
				},1);
		}
};

Events.on(Events.SECTOR_SELECTED, Controller.showSectorSelectionMenu);

