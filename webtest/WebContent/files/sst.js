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
		ENERGY_OF_MOVEMENT_PER_SECTOR: 10,
		ENERGY_PER_SHIELD:1,
		BASE_CONSUMPTION:1,
		ENERGY_OF_MOVEMENT_PER_QUADRANT_PER_WARP: function(speed){
			return speed*speed*speed;
		},
		MAX_WARP_SPEED:4,
		MAX_ENERGY:3000,
		MAX_TORPEDOS:10,
		MAX_REACTOR_OUTPUT:300,
		PHASER_EFFICIENCY:1,
		KLINGON_DISRUPTOR_POWER:50
};
/*
 * Tools
 */

var console = console||{log:function(){}};

var $body = $("body");

String.prototype.startsWith=function(prefix){
	return this.indexOf(prefix)==0;
};

Array.prototype.remove = function() {
    var what, a = arguments, L = a.length, ax;
    while (L && this.length) {
        what = a[--L];
        while ((ax = this.indexOf(what)) !== -1) {
            this.splice(ax, 1);
        }
    }
    return this;
};

Array.prototype.pushUnique = function(element){
	if (element.x && element.y){
		for (var i=0;i<this.length;i++)
			if (this[i].x==element.x && this[i].y==element.y)
				return;
	}
	this.push(element);
};

var Tools={
		screenWidth:-1,
		screenHeight:-1,
		page:$body,
		hashesWithCss:/(computer|status|longrangescan|sector|phasers|starbase)_*/,
		supressNextHistoryEvent:false,
		formatStardate:function(stardate){
			return (Math.round(Computer.stardate*10)/10).toFixed(1);
		},
		scrollIntoView:function(element){
			var offset = element.offset();
			var destination = offset.top-2;
			$(document).scrollTop(destination);
		},
		centerScreen:function(){
			Tools.scrollIntoView($("#page"));
		},
		distance:function(x1,y1,x2,y2){
			return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
		},
		changeHash:function(hash){
			Controller.onHistoryChanged(hash);
		},
		extractPositionFrom:function(text){
			var parts = /.*?(\d)[,_](\d)/.exec(text);
			if (parts)
				return {x:parseInt(parts[1]),y:parseInt(parts[2])};
			return null;
		},
		random:function(max){
			max++;
			var e = Math.exp(1);
			return Math.floor(max*(Math.exp(Math.random())-1)/e);
		},
		
		makeStars:function(){
			var a = new Array();
			for (var i=Tools.random(10);i>0;i--)
				a.pushUnique({
					x:Math.round(Math.random()*7),
					y:Math.round(Math.random()*7),
					star:true,
					name:"a star"
				});
			return a;
		},
		makeKlingons:function(){
			var a = new Array();
			for (var i=Tools.random(3);i>0;i--)
				a.pushUnique({
					x:Math.round(Math.random()*7),
					y:Math.round(Math.random()*7),
					shields:100,
					weaponPower:Constants.KLINGON_DISRUPTOR_POWER,
					klingon:true,
					name:"a klingon raider"
				});
			return a;
		},
		makeStarbases:function(){
			var a = new Array();
			for (var i=Tools.random(1);i>0;i--)
				a.pushUnique({
					x:Math.round(Math.random()*7),
					y:Math.round(Math.random()*7),
					starbase:true,
					name:"a federation starbase"
				});
			return a;
		},
		makeQuadrant:function(regionName,x,y){
			var quadrant= {
				regionName:regionName,
				x:x,
				y:y,
				element:$("#cmd_quadrant_"+x+"_"+y),
				stars:Tools.makeStars(),
				klingons:Tools.makeKlingons(),
				starbases:Tools.makeStarbases()
			};
			return quadrant;
		},
		handleWindowResize:function(){
			var width = $(window).width();
			var height = $(window).height();
			if (Tools.screenHeight == height && Tools.screenWidth == width)
				return;
			Tools.screenHeight = height;
			Tools.screenWidth = width;
			Tools.removePageCss("orientation-horizonal");
			Tools.removePageCss("orientation-vertical");
			if (width>height)
				Tools.addPageCss("orientation-horizontal");
			else
				Tools.addPageCss("orientation-vertical");
		},
		walkLine:function(x0,y0,x1,y1, callback){
			var sx=0;
			var sy=0;
			var err = 0;
			var e2 = 0;
			var dx = Math.abs(x1-x0);
			var dy = Math.abs(y1-y0); 
			if (x0 < x1) sx = 1; else sx = -1;
			if (y0 < y1) sy = 1; else sy = -1;
			err = dx-dy;

			while(true){
				if (!callback(x0,y0))
					break;
				if (x0 == x1 && y0 == y1) break;
			     e2 = 2*err;
			     if (e2 > -dy){ 
			       err = err - dy;
			       x0 = x0 + sx;
			     }
			     if (e2 <  dx){ 
			       err = err + dx;
			       y0 = y0 + sy;
			     };
				};
		},
		findObstruction:function(quadrant, xFrom,yFrom,xTo,yTo){
			var thing = false;
			var firstStep = true;
			var lastGoodX = xFrom;
			var lastGoodY = yFrom;
			Tools.walkLine(xFrom, yFrom, xTo, yTo, function(x,y){
				if (firstStep)
					firstStep = false;
				else{
					thing = StarMap.getAnythingInQuadrantAt(quadrant, x, y);
					if (thing)
						return false;
					}
				lastGoodX = x;
				lastGoodY = y;
				return true;
			});
			if (thing)
				return {obstacle:thing, x:lastGoodX, y:lastGoodY};
		},
		removePageCss:function(css){
			Tools.page.removeClass(css);
		},
		addPageCss:function(css){
			Tools.page.addClass(css);
		},
		updatePageCssWithToken:function(hash){
			if (!Tools.hashesWithCss.test(hash))
				return;
			var css = Tools.hashesWithCss.exec(hash)[1];
			var allClasses = Tools.page.attr("class").split(" ");
			for (var i=0;i<allClasses.length;i++)
				if (allClasses[i].startsWith("page-"))
					Tools.page.removeClass(allClasses[i]);
			Tools.page.addClass("page-"+css);
		},
		hasPageCss:function(css){
			return Tools.page.attr("class").indexOf(css)!=-1;
		},
		handleGlobalClick:function(e){
			var target = $(e.target);
			while(target[0]!=$body[0]){
				var id = target.attr("id");
				if (/cmd_/.test(id)){
					Controller.onHistoryChanged(id);
					return;
				}
				target = target.parent();
			}
		}
};
$body.click(Tools.handleGlobalClick);
$(window).resize(Tools.handleWindowResize);
Tools.handleWindowResize();
/*
 * Starmap
 */

var x=0;
var y=0;

var StarMap={
		quadrants:[
		   Tools.makeQuadrant("Antares I"  ,x=0,y=0),
		   Tools.makeQuadrant("Antares II" ,++x,y),
		   Tools.makeQuadrant("Antares III",++x,y),
		   Tools.makeQuadrant("Antares IV" ,++x,y),
		   Tools.makeQuadrant("Sirius I"   ,++x,y),
		   Tools.makeQuadrant("Sirius II"  ,++x,y),
		   Tools.makeQuadrant("Sirius III" ,++x,y),
		   Tools.makeQuadrant("Sirius IV"  ,++x,y),
		   Tools.makeQuadrant("Rigel I"    ,x=0,++y),
		   Tools.makeQuadrant("Rigel II"   ,++x,y),
		   Tools.makeQuadrant("Rigel III"  ,++x,y),
		   Tools.makeQuadrant("Rigel IV"   ,++x,y),
		   Tools.makeQuadrant("Deneb I"    ,++x,y),
		   Tools.makeQuadrant("Deneb II"   ,++x,y),
		   Tools.makeQuadrant("Deneb  III" ,++x,y),
		   Tools.makeQuadrant("Deneb  IV"  ,++x,y),
		   Tools.makeQuadrant("Procyon I"    ,x=0,++y),
		   Tools.makeQuadrant("Procyon II"   ,++x,y),
		   Tools.makeQuadrant("Procyon III"  ,++x,y),
		   Tools.makeQuadrant("Procyon IV"   ,++x,y),
		   Tools.makeQuadrant("Capella I"    ,++x,y),
		   Tools.makeQuadrant("Capella II"   ,++x,y),
		   Tools.makeQuadrant("Capella III" ,++x,y),
		   Tools.makeQuadrant("Capella IV"  ,++x,y),
		   Tools.makeQuadrant("Vega I"    ,x=0,++y),
		   Tools.makeQuadrant("Vega II"   ,++x,y),
		   Tools.makeQuadrant("Vega III"  ,++x,y),
		   Tools.makeQuadrant("Vega IV"   ,++x,y),
		   Tools.makeQuadrant("Betelgeuse I"    ,++x,y),
		   Tools.makeQuadrant("Betelgeuse II"   ,++x,y),
		   Tools.makeQuadrant("Betelgeuse III" ,++x,y),
		   Tools.makeQuadrant("Betelgeuse IV"  ,++x,y),
		   Tools.makeQuadrant("Canopus I"    ,x=0,++y),
		   Tools.makeQuadrant("Canopus II"   ,++x,y),
		   Tools.makeQuadrant("Canopus III"  ,++x,y),
		   Tools.makeQuadrant("Canopus IV"   ,++x,y),
		   Tools.makeQuadrant("Aldebaran I"    ,++x,y),
		   Tools.makeQuadrant("Aldebaran II"   ,++x,y),
		   Tools.makeQuadrant("Aldebaran III" ,++x,y),
		   Tools.makeQuadrant("Aldebaran IV"  ,++x,y),
		   Tools.makeQuadrant("Altair I"    ,x=0,++y),
		   Tools.makeQuadrant("Altair II"   ,++x,y),
		   Tools.makeQuadrant("Altair III"  ,++x,y),
		   Tools.makeQuadrant("Altair IV"   ,++x,y),
		   Tools.makeQuadrant("Regulus I"    ,++x,y),
		   Tools.makeQuadrant("Regulus II"   ,++x,y),
		   Tools.makeQuadrant("Regulus III" ,++x,y),
		   Tools.makeQuadrant("Regulus IV"  ,++x,y),
		   Tools.makeQuadrant("Sagittarius I"    ,x=0,++y),
		   Tools.makeQuadrant("Sagittarius II"   ,++x,y),
		   Tools.makeQuadrant("Sagittarius III"  ,++x,y),
		   Tools.makeQuadrant("Sagittarius IV"   ,++x,y),
		   Tools.makeQuadrant("Arcturus I"    ,++x,y),
		   Tools.makeQuadrant("Arcturus II"   ,++x,y),
		   Tools.makeQuadrant("Arcturus III" ,++x,y),
		   Tools.makeQuadrant("Arcturus IV"  ,++x,y),
		   Tools.makeQuadrant("Pollux I"    ,x=0,++y),
		   Tools.makeQuadrant("Pollux II"   ,++x,y),
		   Tools.makeQuadrant("Pollux III"  ,++x,y),
		   Tools.makeQuadrant("Pollux IV"   ,++x,y),
		   Tools.makeQuadrant("Spica I"    ,++x,y),
		   Tools.makeQuadrant("Spica II"   ,++x,y),
		   Tools.makeQuadrant("Spica III" ,++x,y),
		   Tools.makeQuadrant("Spica IV"  ,++x,y)
		   ],
		   getQuadrantAt:function(x,y){
			   for (var i=0;i<StarMap.quadrants.length;i++){
				   var quadrant = StarMap.quadrants[i];
				   if (quadrant.x == x && quadrant.y == y)
					   return quadrant;
			   }
		   },
		   getKlingonInQuadrantAt:function(quadrant, x, y){
			   for (var i=0;i<quadrant.klingons.length;i++){
				   var klingon = quadrant.klingons[i];
				   if (klingon.x == x && klingon.y == y)
					   return klingon;
			   }
		   },
		   getAnythingInQuadrantAt:function(quadrant, x, y){
			   var thing = StarMap.getKlingonInQuadrantAt(quadrant, x, y);
			   if (thing)
				   return thing;
			   for (var i=0;i<quadrant.starbases.length;i++){
				   thing = quadrant.starbases[i];
				   if (thing.x == x && thing.y == y)
					   return thing;
			   }
			   for (var i=0;i<quadrant.stars.length;i++){
				   thing = quadrant.stars[i];
				   if (thing.x == x && thing.y == y)
					   return thing;
			   }
		   },
		   isStarbaseAdjacent:function(quadrant, x, y){
			 for (var i=0;i<quadrant.starbases.length;i++){
				 var starbase = quadrant.starbases[i];
				 for (var _x=x-1;_x<=x+1;_x++)  
			     for (var _y=y-1;_y<=y+1;_y++)
			     if (starbase.x==_x && starbase.y==_y)
			    	 return starbase;
			 }
			 return false;
		   }
};

/*
 * StarShip
 */
var StarShip={
	   quadrant:StarMap.quadrants[0],
	   x:0,
	   y:0,
	   energy:Constants.MAX_ENERGY,
	   budget:Constants.MAX_REACTOR_OUTPUT,
	   shields:0, // current shield level. Enemy fire reduces them; they replenish at the beginning of a new round
	   maxShields:100, // maximum level of shields
	   userDefinedShields:0, // that's how much the player set shields. actual shields (see shields property above) might be lower.
	   reactorOutput:Constants.MAX_REACTOR_OUTPUT,
	   torpedos:Constants.MAX_TORPEDOS,
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

/**
 * Quadrant scan
 */
var QuadrantScanScreen={
		element:$("#quadrantscan"),
		updateList:function(symbol, list, formatter){
			for (var i=0;i<list.length;i++){
				var thing = list[i];
				var tile = $("#cmd_sector_"+thing.x+"_"+thing.y);
				var css = formatter(thing);
				tile.attr("class",css);
				tile.html(symbol);
			}
		},
		update:function(quadrant){
			if (QuadrantScanScreen.constructUi!=null){
				QuadrantScanScreen.constructUi();
				QuadrantScanScreen.constructUi=null;
			}
			$("#quadrantscan td").html("&nbsp;");
			QuadrantScanScreen.updateList("&nbsp;*&nbsp;",quadrant.stars, function(star){return "";});
			QuadrantScanScreen.updateList("o-}",quadrant.klingons, function(klingon){
				if (klingon.shields<25)
					return "damage-bad";
				if (klingon.shields<50)
					return "damage-medium";
				if (klingon.shields<75)
					return "damage-light";
				return "";
				});
			QuadrantScanScreen.updateList("&lt;!&gt;",quadrant.starbases, function(starbase){return "";});
			if (StarShip.quadrant === quadrant)
				QuadrantScanScreen.updateList("O=Ξ",[StarShip],function(starhip){return "";});
		},
		constructUi:function(){
			var element = QuadrantScanScreen.element;
			for (var y=0;y<8;y++){
				var tr = $("<tr></tr>");
				element.append(tr);
				for (var x=0;x<8;x++){
					var td = $("<td id='cmd_sector_"+x+"_"+y+"'>&nbsp;</td>");
					tr.append(td);
				}
			}
		},
		selectSectorAt: function(x,y){
			$("#quadrantscan .selected").removeClass("selected");
			$("#cmd_sector_"+x+"_"+y).addClass("selected");
		}
};

/**
 * Long range scan
 */
var LongRangeScanScreen={
		element:$("#longrangescan"),
		show:function(){
			Tools.updatePageCssWithToken("longrangescan");
			for (var i=0;i<StarMap.quadrants.length;i++)
				LongRangeScanScreen.updateQuadrant(StarMap.quadrants[i]);
			$("#longrangescan .has-starship")[0].scrollIntoView();
		},
		updateQuadrant:function(quadrant){
			LongRangeScanScreen.updateElementWithQuadrant(quadrant, quadrant.element);
		},
		updateElementWithQuadrant:function(quadrant, e){
			e.html(quadrant.klingons.length+" "+quadrant.starbases.length+" "+quadrant.stars.length);
			e.removeClass("has-starship");
			if (StarShip.quadrant == quadrant)
				e.addClass("has-starship");
			e.attr("id","cmd_quadrant_"+quadrant.x+"_"+quadrant.y);
		}
};

/**
 * Short range scan
 */

var ShortRangeScanScreen={
	elem:$("#shortrangescan"),
	update:function(quadrant){
		var index = 0;
		var qx = quadrant.x;
		var qy = quadrant.y;
		var cells = $("#shortrangescan td");
		for (var y=qy-1;y<=qy+1;y++)
		for (var x=qx-1;x<=qx+1;x++){
			var cell = $(cells[index]);
			if (x>=0&&x<=7&&y>=0&&y<=7){
				var quadrant = StarMap.getQuadrantAt(x, y);
				LongRangeScanScreen.updateElementWithQuadrant(quadrant, cell);
			} else{
				cell.text("0 0 0");
				cell.attr("id",null);
			}
			index++;
		}
	}
};

/**
 * Computer console
 */
var StatusReport={
		energy:$("#report_energy"),
		energyConsumption:$("#report_energy_consumption"),
		torpedos:$("#report_torpedos"),
		location:$("#report_location"),
		shields:$("#report_shields"),
		stardate:$("#report_stardate"),
		reactor:$("#report_reactor"),
		update:function(){
			StatusReport.energy.text(StarShip.energy);
			StatusReport.energyConsumption.text(Computer.calculateBaseEnergyConsumption);
			StatusReport.torpedos.text(StarShip.torpedos);
			StatusReport.location.text(StarShip.quadrant.regionName+" "+StarShip.quadrant.x+","+StarShip.quadrant.y);
			StatusReport.shields.text(StarShip.shields+ " / "+StarShip.maxShields);
			StatusReport.stardate.text(Tools.formatStardate(Computer.stardate));
			StatusReport.reactor.text("%"+100*(StarShip.reactorOutput/Constants.MAX_REACTOR_OUTPUT));
		}
	};

var CommandBar={
		element:$("#commandbar")
};

var Computer={
		element:$("#computerscreen"),
		stardate:2550,
		updateShieldsIndicator:function(){
			$("#cmd_shields .progress-indicator").css("width",StarShip.shields+"%");
			$("#cmd_shields .max-indicator").css("width",StarShip.maxShields+"%");
		},
		updateStarbaseDockCommand:function(){
			var starbaseNearby = StarMap.isStarbaseAdjacent(StarShip.quadrant, StarShip.x, StarShip.y);
			var isStarbaseNearby = !!starbaseNearby;
			var cmd = $("#cmd_starbase_dock");
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
			QuadrantScanScreen.update(StarShip.quadrant);
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
			console.log("Has enegery budget for "+amount+" / "+StarShip.budget);
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
				return IO.messageAndEndRound("Game over, out of energy");
			}
		}
};

var IO={
	currentCallback:null,
	messages:$("#messages"),
	content:$("#messages .content"),
	messageAndEndRound:function(text){
		IO.message(Controller.endRound,text);
		return false;
	},
	message:function(callback,text){
		if (IO.isMessageShown()){
			IO.content.append("<br>");
		}
		IO.content.append(text);
		Tools.removePageCss("messages-visible");
		Tools.addPageCss("messages-visible");
		IO.currentCallback = callback;
		Tools.scrollIntoView(IO.messages);
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
		IO.currentCallback();
	}
};
$("#messages .single").click(IO.onOkClicked);

/**
 * Klingon AI
 */

var KlingonAI={
		play:function(klingon, quadrant){
			if (StarShip.quadrant != quadrant)
				return;
			// can raider fire on us?
			var obstacle = Tools.findObstruction(quadrant, klingon.x, klingon.y, StarShip.x, StarShip.y);
			if (obstacle)
				KlingonAI.manueverIntoFiringPosition(klingon, StarShip.quadrant);
			else
				KlingonAI.fireOnStarship(klingon);
		},
		manueverIntoFiringPosition:function(klingon, quadrant){
			//find a spot which 1. is empty, 2. raider has a clear shot at us, 3. raider can move to unobstructed 
			for (var x=0;x<8;x++)
			for (var y=0;y<8;y++){
				var thing = StarMap.getAnythingInQuadrantAt(quadrant, x, y);
				if (thing)
					continue;
				var obstacle = Tools.findObstruction(quadrant, x, y, StarShip.x, StarShip.y);
				if (obstacle)
					continue;
				obstacle = Tools.findObstruction(quadrant, klingon.x, klingon.y, x, y);
				if (obstacle)
					continue;
				console.log("found a nice spot at "+x+","+y);
				klingon.x = x;
				klingon.y = y;
				return;
			}
		},
		fireOnStarship:function(klingon){
			StarShip.shields = StarShip.shields - klingon.weaponPower;
			StarShip.maxShields = StarShip.maxShields*4/5;
			StarShip.shields = Math.min(StarShip.shields,StarShip.maxShields);
			if (StarShip.shields < 0){
				return IO.message(function(){}, "Klingon ship destroyed us, game over.");
			}
			return IO.message(function(){}, "Klingon ship fired at us, shields dropped to "+StarShip.shields);
		}
};

/**
 * Controller
 */
var Controller={
		sector:{x:0,y:0},
		currentHistoryToken:null,
		onHistoryChanged:function(token){
			if (token == Controller.currentHistoryToken)
				return;
			Computer.updateStardate();
			if (token.startsWith("cmd_"))
				token = token.substring(4);
			Tools.updatePageCssWithToken(token);
			Controller.currentHistoryToken = token;
			if (""==token){
				Controller.showStartScreen();
			} else
			if (/quadrantscan/.test(token)){
				Controller.quadrantScan();
			} else
			if (/longrangescan/.test(token)){
				Controller.longRangeScan();
			} else
			if (/computer/.test(token)){
				Controller.showComputerScreen();
			} else
			if (/navigate/.test(token)){
				Controller.navigate();
			} else
			if (/sector_\d_\d/.test(token)){
				var position = Tools.extractPositionFrom(token);
				Controller.onSectorSelected(position.x,position.y);
			} else
			if (/quadrant_\d_\d/.test(token)){
				var position = Tools.extractPositionFrom(token);
				var quadrant = StarMap.getQuadrantAt(position.x, position.y);
				Controller.onQuadrantSelected(quadrant);
			} else
			if (/phasers_\d\d\d/.test(token)){
				Controller.firePhasers(parseInt(/phasers_(\d\d\d)/.exec(token)[1]));
			} else
			if (/phasers/.test(token)){
				Controller.selectPhaserStrength();
			} else
			if (/torpedos/.test(token)){
				Controller.fireTorpedos();
			} else
			if (/warp/.test(token)){
				Controller.selectWarpDestination();
			} else
			if (/shields/.test(token)){
				Controller.toggleShieldStrength();
			} else
			if (/status/.test(token)){
				Controller.showStatusReport();
			} else
			if (/starbase_refuel/.test(token)){
				Controller.refuelAtStarbase();
			} else
			if (/starbase_complete/.test(token)){
				Controller.repairAtStarbase();
			} else
			if (/starbase/.test(token)){
				Controller.showStarbaseOptions();
			} else
			if (/cancel/.test(token)){
				Controller.cancel();
			} else
			if (/endround/.test(token)){
				IO.hide();
				Controller.endRound();
			} else
			if (/startround/.test(token)){
				IO.hide();
				Controller.showComputerScreen();
			} else
			if (/sectorselection/.test(token)){
				Controller.showSectorSelectionMenu();
			}
		},
		refuelAtStarbase:function(){
			StarShip.energy = Constants.MAX_ENERGY;
			StarShip.torpedos = Constants.MAX_TORPEDOS;
			StarShip.shields = 0;
			StarShip.maxShields = 100;
			Computer.advanceClock(Constants.DURATION_OF_REFUELING);
			Controller.endRound();
		},
		repairAtStarbase:function(){
			Computer.advanceClock(Constants.DURATION_OF_REPAIRS);
			Controller.refuelAtStarbase();
		},
		showStarbaseOptions:function(){
		},
		onSectorSelected:function(x,y){
			Controller.sector.x = x;
			Controller.sector.y = y;
			QuadrantScanScreen.selectSectorAt(x,y);
			Controller.showSectorSelectionMenu();
		},
		showSectorSelectionMenu:function(){
			$("#cmd_torpedos").text("Photon torpedos ("+StarShip.torpedos+")");
		},
		toggleShieldStrength:function(){
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
		quadrantScan:function(){
			Tools.changeHash("quadrant-scan");
			QuadrantScanScreen.show(StarShip.quadrant);
		},
		longRangeScan:function(){
			Tools.changeHash("long-range-scan");
			LongRangeScanScreen.show();
		},
		onQuadrantSelected:function(quadrant){
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
			StarShip.repositionIfSectorOccupied();
			Controller.startRound();
		},
		cancel:function(){
			Controller.showComputerScreen();
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
			Controller.showComputerScreen();
			Computer.advanceClock(Constants.DURATION_OF_ROUND);
			for (var qi=0;qi<StarMap.quadrants.length;qi++){
				var quadrant = StarMap.quadrants[qi];
				for (var ki=0;ki<quadrant.klingons.length;ki++){
					var klingon = quadrant.klingons[ki];
					KlingonAI.play(klingon, quadrant);
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
		},
		navigate:function(){
			var finalX = Controller.sector.x;
			var finalY = Controller.sector.y;
			var obstacle = Tools.findObstruction(StarShip.quadrant, StarShip.x, StarShip.y, Controller.sector.x, Controller.sector.y);
			if (obstacle){
				finalX = obstacle.x;
				finalY = obstacle.y;
			}
			// movement obstructed?
			distance = Tools.distance(StarShip.x, StarShip.y, finalX, finalY);
			if (distance == 0)
				return Controller.showComputerScreen();
			var consumption = Computer.calculateEnergyConsumptionForMovement(distance);
			if (!Computer.hasEnergyBudgetFor(consumption))
				return;
			Computer.consume(consumption);
			StarShip.x = finalX;
			StarShip.y = finalY;
			Computer.advanceClock(Constants.DURATION_OF_MOVEMENT_PER_SECTOR*distance);
			Controller.endRound();
		},
		firePhasers:function(strength){
			var klingon = StarMap.getKlingonInQuadrantAt(StarShip.quadrant, Controller.sector.x, Controller.sector.y);
			if (!klingon){
				return IO.message(Controller.showSectorSelectionMenu,"No Klingon at that sector");
			}
			var consumption = Computer.calculateEnergyConsumptionForPhasers(strength);
			if (!Computer.hasEnergyBudgetFor(consumption))
				return;
			Computer.consume(consumption);
			var damage = strength/Tools.distance(StarShip.x, StarShip.y, klingon.x, klingon.y);
			klingon.shields-=damage;
			if (klingon.shields<=0){
				StarShip.quadrant.klingons.remove(klingon);
				return IO.messageAndEndRound("Klingon ship destroyed");
			}
			Controller.endRound();
		},
		fireTorpedos:function(){
			if (StarShip.torpedos<1){
				return IO.message(Controller.showComputerScreen, "Out of torpedos");
			}
			if (StarShip.x == Controller.sector.x && StarShip.y == Controller.sector.y){
				return IO.message(Controller.fireTorpedos, "Cannot fire at self");
			}
			var obstacle = Tools.findObstruction(StarShip.quadrant, StarShip.x, StarShip.y, Controller.sector.x, Controller.sector.y);
			StarShip.torpedos--;
			if (obstacle){
				var thing = obstacle.obstacle;
				if (thing.star){
					return IO.messageAndEndRound("Photon torpedo hit star at "+thing.x+","+thing.y);
				}
				if (thing.starbase){
					StarShip.quadrant.starbases.remove(thing);
					return IO.messageAndEndRound("Photon torpedo hit starbase at "+thing.x+","+thing.y);
				}
				if (thing.klingon){
					StarShip.quadrant.klingons.remove(thing);
					return IO.messageAndEndRound("Photon torpedo hit klingon at "+thing.x+","+thing.y);
				}
			};
		},
		selectWarpDestination:function(){
			Controller.longRangeScan();
		},
		warpTo:function(quadrant){
			var distance = Tools.distance(StarShip.quadrant.x, StarShip.quadrant.y, quadrant.x, quadrant.y);
			if (distance==0)
				return Controller.showComputerScreen();
			var consumption = Computer.calculateEnergyConsumptionForWarpDrive(StarShip.quadrant, quadrant);
			var speed = Math.min(distance, Constants.MAX_WARP_SPEED);
			var turns = Constants.DURATION_OF_MOVEMENT_PER_QUADRANT*distance/speed;
			Computer.consume(consumption);
			Computer.advanceClock(turns);
			StarShip.quadrant = quadrant;
			StarShip.repositionIfSectorOccupied();
			Controller.endRound();
		}
};

var _page = $("#page");

function repositionWindowScroll(){
	var doc = window.document;
	var delement = doc.documentElement;
	var scrollOffset = (delement && delement.scrollTop  || doc.body && doc.body.scrollTop  || 0);
	var top = _page.offset().top;
	if (scrollOffset < top){
		Tools.centerScreen();
	}
}

$(window).scroll(repositionWindowScroll);

Controller.startGame();
