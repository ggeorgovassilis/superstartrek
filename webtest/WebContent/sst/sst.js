
/*
 * Tools
 */

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
					shields:Math.round(Math.random()*500)
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
		update:function(quadrant){
			var element = ShortRangeScanScreen.element;
			element.empty();
			for (var y=0;y<8;y++){
				var tr = $("<tr></tr>");
				element.append(tr);
				for (var x=0;x<8;x++){
					content = "";
					for (var i=0;i<quadrant.stars.length;i++){
						var star = quadrant.stars[i];
						if (star.x == x && star.y == y)
							content+="&nbsp;*&nbsp;";
					}
					for (var i=0;i<quadrant.klingons.length;i++){
						var klingon = quadrant.klingons[i];
						if (klingon.x == x && klingon.y == y)
							content+="o-}";
					}
					for (var i=0;i<quadrant.starbases.length;i++){
						var starbase = quadrant.starbases[i];
						if (starbase.x == x && starbase.y == y)
							content+="<!>";
					}
					if (StarShip.quadrant === quadrant && StarShip.x == x && StarShip.y == y){
						content+="O=Ξ";
					}
					if (content=="")
						content="&nbsp;";
					var td = $("<td id='q_"+x+"_"+y+"'><a href='#sector_"+x+","+y+"'>"+content+"</td>");
					tr.append(td);
				}
			}
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

var Computer={
		element:$("#computerscreen"),
		show:function(){
			Tools.setPageCss("computer");
			Controller.resetCommands();
			Tools.addPageCss("top-selection");
			ShortRangeScanScreen.update(StarShip.quadrant);
		}
};
/**
 * Controller
 */
var Controller={
		sector:{x:0,y:0},
		resetCommands:function(){
			Tools.removePageCss("sector-selection");
			Tools.removePageCss("phaser-selection");
			Tools.removePageCss("top-selection");
		},
		onHistoryChanged:function(token){
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
				var quadrant = StarMap.getQuadrantAt(position.x, position.y)
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
			if (/shields_\d\d\d/.test(token)){
				Controller.setShieldStrength(parseInt(/shields_(\d\d\d)/.exec(token)[1]));
			} else
			if (/shields/.test(token)){
				Controller.selectShieldStrength();
			}
		},
		onSectorSelected:function(x,y){
			Controller.resetCommands();
			Tools.addPageCss("sector-selection");
			Controller.sector.x = x;
			Controller.sector.y = y;
		},
		selectShieldStrength:function(){
			Controller.resetCommands();
			Tools.addPageCss("shields-selection");
		},
		setShieldStrength:function(strength){
			StarShip.shields=strength;
			Controller.showStartScreen();
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
		showComputerScreen:function(){
			Tools.changeHash("computer");
			Computer.show();
		},
		showStartScreen:function(){
			Controller.resetCommands();
			Tools.addPageCss("top-selection");
			Controller.showComputerScreen();
		},
		startGame:function(){
			Controller.showStartScreen();
		},
		endRound:function(){
			Controller.showStartScreen();
		},
		selectPhaserStrength:function(){
			Controller.resetCommands();
			Tools.addPageCss("phaser-selection");
		},
		navigate:function(){
			StarShip.x = Controller.sector.x;
			StarShip.y = Controller.sector.y;
			Controller.endRound();
		},
		firePhasers:function(strength){
			console.log("fired phasers with "+strength+" at "+Controller.sector.x+","+Controller.sector.y);
			Controller.endRound();
		},
		fireTorpedos:function(){
			console.log("fired torpedo");
			Controller.endRound();
		},
		selectWarpDestination:function(){
			Controller.resetCommands();
			Controller.longRangeScan();
		},
		warpTo:function(quadrant){
			StarShip.quadrant = quadrant;
			Controller.endRound();
		}
};

$.History.bind(function(state){
	Controller.onHistoryChanged(state);
});

Controller.startGame();


