
/*
 * Tools
 */

var console = console||{log:function(){}};

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
function push(arr,element){
	for (var i=0;i<arr.length;i++){
		var e = arr[i];
		if (e.x == element.x && e.y == element.y)
			return;
	}
	arr.push(element);
}

var Tools={
		page:$("#page"),
		supressNextHistoryEvent:false,
		distance:function(x1,y1,x2,y2){
			return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
		},
		changeHash:function(hash){
			document.location.hash=hash;
		},
		setPageCss:function(css){
			Tools.page.attr("class",css);
		},
		addPageCss:function(css){
			Tools.page.removeClass(css);
			Tools.page.addClass(css);
		},
		removePageCss:function(css){
			Tools.page.removeClass(css);
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
				push(a,{
					x:Math.round(Math.random()*7),
					y:Math.round(Math.random()*7)
				});
			return a;
		},
		makeKlingons:function(){
			var a = new Array();
			for (var i=Tools.random(3);i>0;i--)
				push(a,{
					x:Math.round(Math.random()*7),
					y:Math.round(Math.random()*7),
					shields:100
				});
			return a;
		},
		makeStarbases:function(){
			var a = new Array();
			for (var i=Tools.random(2);i>0;i--)
				push(a,{
					x:Math.round(Math.random()*7),
					y:Math.round(Math.random()*7)
				});
			return a;
		},
		makeQuadrant:function(regionName,x,y){
			var quadrant= {
				regionName:regionName,
				x:x,
				y:y,
				element:$("#"+x+"_"+y),
				stars:Tools.makeStars(),
				klingons:Tools.makeKlingons(),
				starbases:Tools.makeStarbases()
			};
			return quadrant;
		}
		
};

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
		   }
};

/*
 * StarShip
 */
var StarShip={
	   quadrant:StarMap.quadrants[0],
	   x:0,
	   y:0,
	   energy:3000,
	   shields:0,
	   torpedos:10
};

/**
 * Short range scan
 */
var ShortRangeScanScreen={
		element:$("#shortrangescan"),
		updateList:function(symbol, list, formatter){
			for (var i=0;i<list.length;i++){
				var thing = list[i];
				var tile = $("#q_"+thing.x+"_"+thing.y);
				var css = formatter(thing);
				tile.attr("class",css);
				tile.html(symbol);
			}
		},
		update:function(quadrant){
			if (ShortRangeScanScreen.constructUi!=null){
				ShortRangeScanScreen.constructUi();
				ShortRangeScanScreen.constructUi=null;
			}
			$("#shortrangescan a").html("&nbsp;");
			ShortRangeScanScreen.updateList("&nbsp;*&nbsp;",quadrant.stars, function(star){return "";});
			ShortRangeScanScreen.updateList("o-}",quadrant.klingons, function(klingon){
				if (klingon.shields<25)
					return "damage-bad";
				if (klingon.shields<50)
					return "damage-medium";
				if (klingon.shields<75)
					return "damage-light";
				return "";
				});
			ShortRangeScanScreen.updateList("&lt;!&gt;",quadrant.starbases, function(starbase){return "";});
			if (StarShip.quadrant === quadrant)
				ShortRangeScanScreen.updateList("O=Ξ",[StarShip],function(starhip){return "";});
		},
		constructUi:function(){
			var element = ShortRangeScanScreen.element;
			for (var y=0;y<8;y++){
				var tr = $("<tr></tr>");
				element.append(tr);
				for (var x=0;x<8;x++){
					var td = $("<td><a id='q_"+x+"_"+y+"' href='#sector_"+x+","+y+"'></a></td>");
					tr.append(td);
				}
			}
		},
		selectSectorAt: function(x,y){
			$("#shortrangescan .selected").removeClass("selected");
			$("#q_"+x+"_"+y).addClass("selected");
		}
};

/**
 * Long range scan
 */
var LongRangeScanScreen={
		element:$("#longrangescan"),
		show:function(){
			Tools.setPageCss("long-range-scan");
			for (var i=0;i<StarMap.quadrants.length;i++)
				LongRangeScanScreen.updateQuadrant(StarMap.quadrants[i]);
		},
		updateQuadrant:function(quadrant){
			var e = quadrant.element;
			e.html("<a href='#quadrant_"+quadrant.x+","+quadrant.y+"'>"+quadrant.klingons.length+" "+quadrant.starbases.length+" "+quadrant.stars.length+"</a>");
			e.removeClass("has-starship");
			if (StarShip.quadrant == quadrant)
				e.addClass("has-starship");
		}
};

/**
 * Computer console
 */
var StatusReport={
		energy:$("#report_energy"),
		torpedos:$("#report_torpedos"),
		location:$("#report_location"),
		shields:$("#report_shields"),
		update:function(){
			StatusReport.energy.text(StarShip.energy);
			StatusReport.torpedos.text(StarShip.torpedos);
			StatusReport.location.text(StarShip.quadrant.regionName+" "+StarShip.quadrant.x+","+StarShip.quadrant.y);
			StatusReport.shields.text(StarShip.shields);
		}
	};

var CommandBar={
		element:$("#commandbar"),
		resetCommands:function(){
			Tools.removePageCss("sector-selection");
			Tools.removePageCss("phaser-selection");
			Tools.removePageCss("top-selection");
		},
		scrollIntoView:function(){
			var offset = CommandBar.element.offset();
			var destination = offset.top;
			$(document).scrollTop(destination);
		}
};

var Computer={
		element:$("#computerscreen"),
		show:function(){
			Tools.setPageCss("computer");
			Controller.resetCommands();
			Tools.addPageCss("top-selection");
			ShortRangeScanScreen.update(StarShip.quadrant);
		},
		calculateBaseEnergyConsumption:function(){
			return StarShip.shields;
		},
		calculateEnergyConsumptionForMovement:function(xFrom,yFrom,xTo,yTo){
			var distance = Tools.distance(xFrom,yFrom,xTo,yTo);
			return distance*10;
		},
		calculateEnergyConsumptionForWarpDrive:function(quadrantFrom, quadrantTo){
			var distance = Tools.distance(quadrantFrom.x,quadrantFrom.y,quadrantTo.x,quadrantTo.y);
			return distance*100;
		},
		calculateEnergyConsumptionForPhasers:function(strength){
			return strength;
		},
		consume:function(energy){
			StarShip.energy-=energy;
			StarShip.energy=Math.floor(StarShip.energy);
			if (StarShip.energy<=0)
				IO.message("Game over, out of energy");
		}
};

var IO={
	message:function(text){
		alert(text);
	}	
};

/**
 * Controller
 */
var Controller={
		sector:{x:0,y:0},
		currentHistoryToken:null,
		resetCommands:function(){
			CommandBar.resetCommands();
		},
		onHistoryChanged:function(token){
			if (token == Controller.currentHistoryToken)
				return;
			Controller.currentHistoryToken = token;
			if (""==token){
				Controller.showStartScreen();
			} else
			if (/short-range-scan/.test(token)){
				Controller.shortRangeScan();
			} else
			if (/long-range-scan/.test(token)){
				Controller.longRangeScan();
			} else
			if (/computer/.test(token)){
				Controller.showComputerScreen();
			} else
			if (/navigate/.test(token)){
				Controller.navigate();
			} else
			if (/sector_\d,\d/.test(token)){
				var position = Tools.extractPositionFrom(token);
				Controller.onSectorSelected(position.x,position.y);
			} else
			if (/quadrant_\d,\d/.test(token)){
				var position = Tools.extractPositionFrom(token);
				var quadrant = StarMap.getQuadrantAt(position.x, position.y);
				Controller.warpTo(quadrant);
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
			if (/cancel/.test(token)){
				Controller.cancel();
			}
		},
		onSectorSelected:function(x,y){
			Controller.resetCommands();
			$("#torpedos").text("Photon torpedos ("+StarShip.torpedos+")");
			Tools.addPageCss("sector-selection");
			Controller.sector.x = x;
			Controller.sector.y = y;
			ShortRangeScanScreen.selectSectorAt(x,y);
		},
		toggleShieldStrength:function(){
			var shields = StarShip.shields;
			shields+=25;
			shields=shields%101;
			shields-=shields%25;
			$("#shields").text("Shields "+(shields?shields+"%":"off"));
			StarShip.shields = shields;
			Controller.gotoStartScreen();
		},
		shortRangeScan:function(){
			Tools.changeHash("short-range-scan");
			ShortRangeScanScreen.show(StarShip.quadrant);
		},
		longRangeScan:function(){
			Tools.changeHash("long-range-scan");
			LongRangeScanScreen.show();
		},
		onQuadrantSelected:function(quadrant){
			console.log(quadrant);
		},
		gotoComputerScreen:function(){
			Tools.changeHash("computer");
		},
		showComputerScreen:function(){
			Computer.show();
		},
		showStatusReport:function(){
			Tools.changeHash("status");
			Tools.setPageCss("status-report");
			StatusReport.update();
		},
		gotoStartScreen:function(){
			Controller.gotoComputerScreen();
		},
		showStartScreen:function(){
			Controller.showComputerScreen();
		},
		startGame:function(){
			Controller.gotoStartScreen();
		},
		endRound:function(){
			var consumption = Computer.calculateBaseEnergyConsumption();
			Computer.consume(consumption);
			Controller.gotoStartScreen();
		},
		selectPhaserStrength:function(){
			Controller.resetCommands();
			Tools.addPageCss("phaser-selection");
		},
		navigate:function(){
			var consumption = Computer.calculateEnergyConsumptionForMovement(StarShip.x, StarShip.y, Controller.sector.x, Controller.sector.y);
			Computer.consume(consumption);
			StarShip.x = Controller.sector.x;
			StarShip.y = Controller.sector.y;
			Controller.endRound();
		},
		firePhasers:function(strength){
			var klingon = StarMap.getKlingonInQuadrantAt(StarShip.quadrant, Controller.sector.x, Controller.sector.y);
			if (!klingon){
				IO.message("No Klingon at that sector");
				return;
			}
			var consumption = Computer.calculateEnergyConsumptionForPhasers(strength);
			Computer.consume(consumption);
			var damage = strength/Tools.distance(StarShip.x, StarShip.y, klingon.x, klingon.y);
			console.log(damage);
			klingon.shields-=damage;
			if (klingon.shields<=0){
				IO.message("Klingon ship destroyed");
				StarShip.quadrant.klingons.remove(klingon);
			}
			Controller.endRound();
		},
		fireTorpedos:function(){
			if (StarShip.torpedos<1){
				IO.message("Out of torpedos");
				return;
			}
			StarShip.torpedos--;
			Controller.endRound();
		},
		selectWarpDestination:function(){
			Controller.resetCommands();
			Controller.longRangeScan();
		},
		warpTo:function(quadrant){
			var consumption = Computer.calculateEnergyConsumptionForWarpDrive(StarShip.quadrant, quadrant);
			Computer.consume(consumption);
			StarShip.quadrant = quadrant;
			
			Controller.endRound();
		}
};

$.History.bind(function(state){
	Controller.onHistoryChanged(state);
});

Controller.startGame();

