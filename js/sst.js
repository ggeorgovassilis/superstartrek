/**
 * Constants.
 * Refer to wikipedia articles on stardate and warpspeed for why constants were assigned their current values.
 * Phasers lose power over distance in the original game. No information about disruptors.
 */

var Constants = {
		DURATION_OF_MOVEMENT_PER_SECTOR: 0.05,
		DURATION_OF_MOVEMENT_PER_QUADRANT: 1,
		DURATION_OF_ROUND:1,
		DURATION_OF_REFUELING:2,
		DURATION_OF_REPAIRS:4,
		ENERGY_OF_MOVEMENT_PER_SECTOR: 20,
		ENERGY_PER_SHIELD:2,
		BASE_CONSUMPTION:1,
		ENERGY_OF_MOVEMENT_PER_QUADRANT_PER_WARP: function(speed){
			return 10+speed*speed*speed;
		},
		MAX_WARP_SPEED:4,
		MAX_ENERGY:3000,
		MAX_TORPEDOS:10,
		MAX_TORPEDO_DAMAGE:50,
		MAX_IMPULSE_SPEED:3,
		MAX_REACTOR_OUTPUT:350,
		PHASER_EFFICIENCY:1,
		KLINGON_DISRUPTOR_POWER:30,
		KLINGON_IMPULSE_SPEED:2,
		DISRUPTOR_RANGE:3,
		SMALL_HEIGHT:450,
		SMALL_WIDTH:621,
		CHANCE_OF_STARBASE_IN_QUADRANT:0.08,
		CHANCE_OF_KLINGON_IN_QUADRANT:0.3,
		MAX_KLINGON_SHIELD:80,
};
$body.fastClick(Tools.handleGlobalClick);

$window.resize(Tools.handleWindowResize);
Tools.handleWindowResize();
/*
 * Starmap
 */

var x=0;
var y=0;

var StarMap={
		constructQuadrants:function(){
		StarMap.quadrants =
		[
		Setup.makeQuadrant("Antares I"  ,x=0,y=0),
		Setup.makeQuadrant("Antares II" ,++x,y),
		Setup.makeQuadrant("Antares III",++x,y),
		Setup.makeQuadrant("Antares IV" ,++x,y),
		Setup.makeQuadrant("Sirius I"   ,++x,y),
		Setup.makeQuadrant("Sirius II"  ,++x,y),
		Setup.makeQuadrant("Sirius III" ,++x,y),
		Setup.makeQuadrant("Sirius IV"  ,++x,y),
		Setup.makeQuadrant("Rigel I"    ,x=0,++y),
		Setup.makeQuadrant("Rigel II"   ,++x,y),
		Setup.makeQuadrant("Rigel III"  ,++x,y),
		Setup.makeQuadrant("Rigel IV"   ,++x,y),
		Setup.makeQuadrant("Deneb I"    ,++x,y),
		Setup.makeQuadrant("Deneb II"   ,++x,y),
		Setup.makeQuadrant("Deneb  III" ,++x,y),
		Setup.makeQuadrant("Deneb  IV"  ,++x,y),
		Setup.makeQuadrant("Procyon I"    ,x=0,++y),
		Setup.makeQuadrant("Procyon II"   ,++x,y),
		Setup.makeQuadrant("Procyon III"  ,++x,y),
		Setup.makeQuadrant("Procyon IV"   ,++x,y),
		Setup.makeQuadrant("Capella I"    ,++x,y),
		Setup.makeQuadrant("Capella II"   ,++x,y),
		Setup.makeQuadrant("Capella III" ,++x,y),
		Setup.makeQuadrant("Capella IV"  ,++x,y),
		Setup.makeQuadrant("Vega I"    ,x=0,++y),
		Setup.makeQuadrant("Vega II"   ,++x,y),
		Setup.makeQuadrant("Vega III"  ,++x,y),
		Setup.makeQuadrant("Vega IV"   ,++x,y),
		Setup.makeQuadrant("Betelgeuse I"    ,++x,y),
		Setup.makeQuadrant("Betelgeuse II"   ,++x,y),
		Setup.makeQuadrant("Betelgeuse III" ,++x,y),
		Setup.makeQuadrant("Betelgeuse IV"  ,++x,y),
		Setup.makeQuadrant("Canopus I"    ,x=0,++y),
		Setup.makeQuadrant("Canopus II"   ,++x,y),
		Setup.makeQuadrant("Canopus III"  ,++x,y),
		Setup.makeQuadrant("Canopus IV"   ,++x,y),
		Setup.makeQuadrant("Aldebaran I"    ,++x,y),
		Setup.makeQuadrant("Aldebaran II"   ,++x,y),
		Setup.makeQuadrant("Aldebaran III" ,++x,y),
		Setup.makeQuadrant("Aldebaran IV"  ,++x,y),
		Setup.makeQuadrant("Altair I"    ,x=0,++y),
		Setup.makeQuadrant("Altair II"   ,++x,y),
		Setup.makeQuadrant("Altair III"  ,++x,y),
		Setup.makeQuadrant("Altair IV"   ,++x,y),
		Setup.makeQuadrant("Regulus I"    ,++x,y),
		Setup.makeQuadrant("Regulus II"   ,++x,y),
		Setup.makeQuadrant("Regulus III" ,++x,y),
		Setup.makeQuadrant("Regulus IV"  ,++x,y),
		Setup.makeQuadrant("Sagittarius I"    ,x=0,++y),
		Setup.makeQuadrant("Sagittarius II"   ,++x,y),
		Setup.makeQuadrant("Sagittarius III"  ,++x,y),
		Setup.makeQuadrant("Sagittarius IV"   ,++x,y),
		Setup.makeQuadrant("Arcturus I"    ,++x,y),
		Setup.makeQuadrant("Arcturus II"   ,++x,y),
		Setup.makeQuadrant("Arcturus III" ,++x,y),
		Setup.makeQuadrant("Arcturus IV"  ,++x,y),
		Setup.makeQuadrant("Pollux I"    ,x=0,++y),
		Setup.makeQuadrant("Pollux II"   ,++x,y),
		Setup.makeQuadrant("Pollux III"  ,++x,y),
		Setup.makeQuadrant("Pollux IV"   ,++x,y),
		Setup.makeQuadrant("Spica I"    ,++x,y),
		Setup.makeQuadrant("Spica II"   ,++x,y),
		Setup.makeQuadrant("Spica III" ,++x,y),
		Setup.makeQuadrant("Spica IV"  ,++x,y)
		];
			},
		   getQuadrantAt:function(x,y){
			   for (var i=0;i<StarMap.quadrants.length;i++){
				   var quadrant = StarMap.quadrants[i];
				   if (quadrant.x === x && quadrant.y === y)
					   return quadrant;
			   }
		   },
		   getKlingonInQuadrantAt:function(quadrant, x, y){
			   for (var i=0;i<quadrant.klingons.length;i++){
				   var klingon = quadrant.klingons[i];
				   if (klingon.x === x && klingon.y === y)
					   return klingon;
			   }
		   },
		   getAnythingInQuadrantAt:function(quadrant, x, y){
			   var thing = StarMap.getKlingonInQuadrantAt(quadrant, x, y);
			   if (thing)
				   return thing;
			   for (var i=0;i<quadrant.starbases.length;i++){
				   thing = quadrant.starbases[i];
				   if (thing.x === x && thing.y === y)
					   return thing;
			   }
			   for (var i=0;i<quadrant.stars.length;i++){
				   thing = quadrant.stars[i];
				   if (thing.x === x && thing.y === y)
					   return thing;
			   }
		   },
		   isStarbaseAdjacent:function(quadrant, x, y){
			 for (var i=0;i<quadrant.starbases.length;i++){
				 var starbase = quadrant.starbases[i];
				 for (var _x=x-1;_x<=x+1;_x++)  
			     for (var _y=y-1;_y<=y+1;_y++)
			     if (starbase.x===_x && starbase.y===_y)
			    	 return starbase;
			 }
			 return false;
		   },
		   countKlingons:function(){
			   var count = 0;
			   for (var i=0;i<StarMap.quadrants.length;i++)
				   count+=StarMap.quadrants[i].klingons.length;
			   return count;
		   }
};

/*
 * StarShip
 */
var StarShip={
		setup:function(){
		StarShip.quadrant=StarMap.quadrants[0];
		StarShip.x=0;
		StarShip.y=0;
		StarShip.energy=Constants.MAX_ENERGY;
		StarShip.budget=Constants.MAX_REACTOR_OUTPUT;
		StarShip.shields=0; // current shield level. Enemy fire reduces them; they replenish at the beginning of a new round
		StarShip.maxShields=100; // maximum level of shields
		StarShip.userDefinedShields=0; // that's how much the player set shields. actual shields (see shields property above) might be lower.
		StarShip.reactorOutput=Constants.MAX_REACTOR_OUTPUT;
		StarShip.torpedos=Constants.MAX_TORPEDOS;
		StarShip.maxImpulse=Constants.MAX_IMPULSE_SPEED;
		},
	   repositionIfSectorOccupied:function(){
		   var newX = StarShip.x;
		   var newY = StarShip.y;
		   while (StarMap.getAnythingInQuadrantAt(StarShip.quadrant, newX, newY)){
			   newX = Math.min(7,Math.max(0,Math.round(newX + 1-2*Math.random())));
			   newY = Math.min(7,Math.max(0,Math.round(newY + 1-2*Math.random())));
		   }
		   StarShip.x = newX;
		   StarShip.y = newY;
	   }
};



var CommandBar={
		element:$("#commandbar")
};

var Intro={
	visible:false,
	show:function(){
		Intro.visible = true;
		Tools.updatePageCssWithToken("intro");
		var button = $("#cmd_leaveIntro");
	},
	hide:function(){
		Intro.visible = false;
	}
};

var Computer={
		element:$("#computerscreen"),
		stardate:2550,
		updateEnergyIndicator:function(){
			var progress = 100*StarShip.energy/Constants.MAX_ENERGY;
			$("#cmd_showStatusReport .progress-indicator").css("width",progress+"%");
		},
		updateShieldsIndicator:function(){
			$("#cmd_toggleShields .progress-indicator").css("width",StarShip.shields+"%");
			$("#cmd_toggleShields .max-indicator").css("width",StarShip.maxShields+"%");
		},
		updateStarbaseDockCommand:function(){
			var starbaseNearby = StarMap.isStarbaseAdjacent(StarShip.quadrant, StarShip.x, StarShip.y);
			var isStarbaseNearby = !!starbaseNearby;
			var cmd = $("#cmd_dockWithStarbase");
			if (isStarbaseNearby)
				cmd.addClass("starbase-nearby");
			else
				cmd.removeClass("starbase-nearby");
		},
		advanceClock:function(duration){
			Computer.stardate+=duration;
		},
		updateStardate:function(){
			var stardateFormatted = Tools.formatStardate(Computer.stardate);
			$("#stardate").text(stardateFormatted + " "+StarShip.budget);
		},
		show:function(){
			Tools.updatePageCssWithToken("computer");
			Computer.updateStarbaseDockCommand();
			Computer.updateShieldsIndicator();
			Computer.updateEnergyIndicator();
			ShortRangeScanScreen.update(StarShip.quadrant);
		},
		calculateBaseEnergyConsumption:function(){
			return StarShip.shields*Constants.ENERGY_PER_SHIELD + Constants.BASE_CONSUMPTION;
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
			if (StarShip.budget < amount){
				IO.message(function(){}, "Cannot execute command, reactor capacity reached");
				return false;
			};
			return true;
		},
		consume:function(energy){
			StarShip.budget-=energy;
			StarShip.energy-=energy;
			StarShip.energy=Math.floor(StarShip.energy);
			if (StarShip.energy<=0){
				return IO.gameOverMessage("Game over, out of energy");
			}
		}
};

var IO={
	currentCallback:null,
	mute:false,
	onKeyPressed:function(e){
		IO.hide();
	},
	messages:$("#messages"),
	content:$("#messages .content"),
	endRound:function(){
		IO.currentCallback = Controller.endRound;
		return false;
	},
	nothing:function(){
		return true;
	},
	messageAndEndRound:function(text){
		IO.message(Controller.endRound,text);
		return false;
	},
	message:function(callback,text){
		if (IO.mute)
			return;
		if (IO.isMessageShown()){
			IO.content.append("<br>");
		}
		IO.content.append(text);
		Tools.removePageCss("messages-visible");
		Tools.addPageCss("messages-visible");
		IO.currentCallback = callback;
		repositionWindowScroll();
		IO.messages.find(".single").focus();
		return false;
	},
	gameOverMessage:function(text){
		IO.message(null, text);
		IO.message(Controller.startGame, "Click OK to start a new game");
		IO.mute=true;
		return false;
	},
	hide:function(){
		IO.content.empty();
		Tools.removePageCss("messages-visible");
	},
	isMessageShown:function(){
		return Tools.hasPageCss("messages-visible");
	},
	onOkClicked:function(){
		IO.hide();
		if (IO.currentCallback)
			IO.currentCallback();
	}
};
$("#messages .single").fastClick(IO.onOkClicked);


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
			Tools.updatePageCssWithToken(method);
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
			StarShip.energy = Constants.MAX_ENERGY;
			StarShip.torpedos = Constants.MAX_TORPEDOS;
			StarShip.shields = 0;
			Computer.advanceClock(Constants.DURATION_OF_REFUELING);
			Controller.endRound();
		},
		repairAtStarbase:function(){
			Computer.advanceClock(Constants.DURATION_OF_REPAIRS);
			StarShip.maxShields = 100;
			StarShip.maxImpulse = Constants.MAX_IMPULSE_SPEED;
			Controller.refuelAtStarbase();
		},
		selectSector:function(x,y){
			Controller.sector.x = x;
			Controller.sector.y = y;
			ShortRangeScan.selectSectorAt(x,y);
			Controller.showSectorSelectionMenu();
		},
		showSectorSelectionMenu:function(){
			$("#cmd_fireTorpedos").text("Photon torpedos ("+StarShip.torpedos+")");
		},
		toggleShields:function(){
			var shields = StarShip.userDefinedShields;
			var delta = -shields;
			if (shields == StarShip.maxShields)
				shields = 0;
			else
				shields=Math.min(StarShip.budget,Math.min(shields+25, StarShip.maxShields));
			delta+=shields;
			Computer.consume(delta);
			StarShip.userDefinedShields = shields;
			StarShip.shields = shields;
			Computer.updateShieldsIndicator();
			Controller.showStartScreen();
		},
		showLongRangeScan:function(){
			LongRangeScanScreen.show();
		},
		selectQuadrant:function(x,y){
			var quadrant = StarMap.getQuadrantAt(x, y);
			Controller.warpTo(quadrant);
		},
		showComputerScreen:function(){
			Computer.advanceClock(0);
			Computer.show();
			Tools.centerScreen();
		},
		showStatusReport:function(){
			StatusReport.update();
		},
		showStartScreen:function(){
			Controller.showComputerScreen();
		},
		startGame:function(){
			IO.mute = false;
			Computer.stardate=2550;
			StarMap.constructQuadrants();
			StarShip.setup();
			StarShip.repositionIfSectorOccupied();
			$window.trigger("init");
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
		startRound:function(){
			StarShip.budget=StarShip.reactorOutput;
			StarShip.shields = StarShip.userDefinedShields;
			var consumption = Computer.calculateBaseEnergyConsumption();
			Computer.consume(consumption);
			StarShip.shields = Math.min(StarShip.shields,StarShip.maxShields);
			if (!IO.isMessageShown())
				Controller.showComputerScreen();
			else IO.message(Controller.showComputerScreen,"");
			Controller.showStartScreen();
		},
		endRound:function(){
			if (StarMap.countKlingons()===0){
				return IO.gameOverMessage("All Klingons destroyed!");
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
				IO.message(Controller.startRound, "");
			else
				Controller.startRound();
		},
		selectPhaserStrength:function(){
			var klingon = StarMap.getKlingonInQuadrantAt(StarShip.quadrant, Controller.sector.x, Controller.sector.y);
			if (!klingon){
				return IO.message(Controller.showSectorSelectionMenu,"No Klingon at that sector");
			}
		}
};

var _page = $("#page");

function repositionWindowScroll(){
	var doc = window.document;
	var delement = doc.documentElement;
	var scrollOffset = (delement && delement.scrollTop  || doc.body && doc.body.scrollTop  || 0);
	var top = _page.offset().top;
	if (scrollOffset <= top){
		Tools.centerScreen();
	}
}

//window.onbeforeunload = function(e){
//		return ""; 
//};

$(window).scroll(repositionWindowScroll);
