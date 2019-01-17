/**
 * Controller
 */
var Controller={
		sector:{x:0,y:0},
		currentToken:null,
		onClickedActivityToken:function(token){
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
			if (Controller[method])
			try{
				Controller.currentHistoryToken = method;
				(Controller[method])(arg1, arg2);
			}
			catch(ex){
				console.log("error for method "+method);
				console.log(ex);
				throw ex;
			}
		},
		dockWithStarbase:function(){
			Computer.advanceClock(Constants.DURATION_OF_REPAIRS);
			var sx = Enterprise.quadrant.starbases[0].x;
			var sy = Enterprise.quadrant.starbases[0].y;
			var moved = false;
			while (Tools.distance(sx,sy,Enterprise.x,Enterprise.y)>2 || StarMap.getAnythingInQuadrantAt(Enterprise.quadrant, Enterprise.x, Enterprise.y)!=Enterprise){
				Enterprise.x = Math.max(0,Math.min(7,sx+1-Tools.random(2)));
				Enterprise.y = Math.max(0,Math.min(sy+1-Tools.random(2)));
				moved = true;
			}
			Enterprise.repairAtStarbase();
			Controller.endTurn();
			if (moved)
				Events.trigger(Events.ENTERPRISE_MOVED);
			Events.trigger(Events.ENTERPRISE_REPAIRED);
		},
		repairProvisionally:function(){
			Enterprise.repairProvisionally()
			return IO.endTurn();
		},
		showSectorSelectionMenu:function(s){
			Controller.sector.x = s.x;
			Controller.sector.y = s.y;
			Tools.addPageCss("sector-selected");
		},
		toggleShields:function(){
			Enterprise.toggleShields();
		},
		longrangescan:function(){
			LongRangeScanScreen.show();
		},
		selectQuadrant:function(x,y){
			var quadrant = StarMap.getQuadrantAt(x, y);
			Navigation.warpTo(quadrant);
		},
		computer:function(){ //#computer
			Controller.showComputerScreen();
		},
		showComputerScreen:function(){
			Computer.advanceClock(0);
			Tools.removePageCss("sector-selected");
			Computer.show();
		},
		statusreport:function(){ //#statusreport
			StatusReport.update();
		},
		showStartScreen:function(){
			Controller.showComputerScreen();
		},
		startGame:function(){
			Setup.decorateUI();
			window.location.hash="#";
			Computer.stardate=2550;
			StarMap.constructQuadrants();
			Enterprise.setup();
			Enterprise.repositionIfSectorOccupied();
			Events.trigger(Events.START_GAME);
			Events.trigger(Events.SETTINGS_CHANGED);
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
			Enterprise.resetForNewTurn();
			var consumption = Computer.calculateBaseEnergyConsumption();
			Computer.consume(consumption);
			Events.trigger(Events.TURN_STARTS);
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
			if (Enterprise.fireAtWill)
				Tools.addPageCss("fireAtWill");
			else
				Tools.removePageCss("fireAtWill");
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
			window.onbeforeunload = null;
			Controller.newTurn=Controller.gameOver;
			if (e) IO.message(e.message);
			else
				window.setTimeout(function(){
					window.location.hash="";
					window.location.reload();
				},1);
		},
		scanSector:function(){
			Events.trigger(Events.SECTOR_SCAN,{quadrant:Enterprise.quadrant,x:Controller.sector.x,y:Controller.sector.y});
		}
};

Events.on(Events.SECTOR_SELECTED, Controller.showSectorSelectionMenu);
Events.on(Events.SETTINGS_CHANGED, Controller.updateFireAtWillButton);