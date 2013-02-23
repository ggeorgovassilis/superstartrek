
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
					x:Math.round(Math.random()*10),
					y:Math.round(Math.random()*10)
				});
			return a;
		},
		makeKlingons:function(){
			var a = new Array();
			for (var i=Tools.random(3);i>0;i--)
				push(a,{
					x:Math.round(Math.random()*10),
					y:Math.round(Math.random()*10),
					shields:Math.round(Math.random()*500)
				});
			return a;
		},
		makeStarbases:function(){
			var a = new Array();
			for (var i=Tools.random(2);i>0;i--)
				push(a,{
					x:Math.round(Math.random()*10),
					y:Math.round(Math.random()*10)
				});
			return a;
		},
		makeSector:function(name,x,y){
			var sector= {
				name:name,
				x:x,
				y:y,
				element:$("#"+x+"_"+y),
				stars:Tools.makeStars(),
				klingons:Tools.makeKlingons(),
				starbases:Tools.makeStarbases()
			};
			return sector;
		}
		
};

/*
 * Starmap
 */

var x=0;
var y=0;

var StarMap={
		sectors:[
		   Tools.makeSector("Antares I"  ,x=0,y=0),
		   Tools.makeSector("Antares II" ,++x,y),
		   Tools.makeSector("Antares III",++x,y),
		   Tools.makeSector("Antares IV" ,++x,y),
		   Tools.makeSector("Sirius I"   ,++x,y),
		   Tools.makeSector("Sirius II"  ,++x,y),
		   Tools.makeSector("Sirius III" ,++x,y),
		   Tools.makeSector("Sirius IV"  ,++x,y),
		   Tools.makeSector("Rigel I"    ,x=0,++y),
		   Tools.makeSector("Rigel II"   ,++x,y),
		   Tools.makeSector("Rigel III"  ,++x,y),
		   Tools.makeSector("Rigel IV"   ,++x,y),
		   Tools.makeSector("Deneb I"    ,++x,y),
		   Tools.makeSector("Deneb II"   ,++x,y),
		   Tools.makeSector("Deneb  III" ,++x,y),
		   Tools.makeSector("Deneb  IV"  ,++x,y),
		   Tools.makeSector("Procyon I"    ,x=0,++y),
		   Tools.makeSector("Procyon II"   ,++x,y),
		   Tools.makeSector("Procyon III"  ,++x,y),
		   Tools.makeSector("Procyon IV"   ,++x,y),
		   Tools.makeSector("Capella I"    ,++x,y),
		   Tools.makeSector("Capella II"   ,++x,y),
		   Tools.makeSector("Capella III" ,++x,y),
		   Tools.makeSector("Capella IV"  ,++x,y),
		   Tools.makeSector("Vega I"    ,x=0,++y),
		   Tools.makeSector("Vega II"   ,++x,y),
		   Tools.makeSector("Vega III"  ,++x,y),
		   Tools.makeSector("Vega IV"   ,++x,y),
		   Tools.makeSector("Betelgeuse I"    ,++x,y),
		   Tools.makeSector("Betelgeuse II"   ,++x,y),
		   Tools.makeSector("Betelgeuse III" ,++x,y),
		   Tools.makeSector("Betelgeuse IV"  ,++x,y),
		   Tools.makeSector("Canopus I"    ,x=0,++y),
		   Tools.makeSector("Canopus II"   ,++x,y),
		   Tools.makeSector("Canopus III"  ,++x,y),
		   Tools.makeSector("Canopus IV"   ,++x,y),
		   Tools.makeSector("Aldebaran I"    ,++x,y),
		   Tools.makeSector("Aldebaran II"   ,++x,y),
		   Tools.makeSector("Aldebaran III" ,++x,y),
		   Tools.makeSector("Aldebaran IV"  ,++x,y),
		   Tools.makeSector("Altair I"    ,x=0,++y),
		   Tools.makeSector("Altair II"   ,++x,y),
		   Tools.makeSector("Altair III"  ,++x,y),
		   Tools.makeSector("Altair IV"   ,++x,y),
		   Tools.makeSector("Regulus I"    ,++x,y),
		   Tools.makeSector("Regulus II"   ,++x,y),
		   Tools.makeSector("Regulus III" ,++x,y),
		   Tools.makeSector("Regulus IV"  ,++x,y),
		   Tools.makeSector("Sagittarius I"    ,x=0,++y),
		   Tools.makeSector("Sagittarius II"   ,++x,y),
		   Tools.makeSector("Sagittarius III"  ,++x,y),
		   Tools.makeSector("Sagittarius IV"   ,++x,y),
		   Tools.makeSector("Arcturus I"    ,++x,y),
		   Tools.makeSector("Arcturus II"   ,++x,y),
		   Tools.makeSector("Arcturus III" ,++x,y),
		   Tools.makeSector("Arcturus IV"  ,++x,y),
		   Tools.makeSector("Pollux I"    ,x=0,++y),
		   Tools.makeSector("Pollux II"   ,++x,y),
		   Tools.makeSector("Pollux III"  ,++x,y),
		   Tools.makeSector("Pollux IV"   ,++x,y),
		   Tools.makeSector("Spica I"    ,++x,y),
		   Tools.makeSector("Spica II"   ,++x,y),
		   Tools.makeSector("Spica III" ,++x,y),
		   Tools.makeSector("Spica IV"  ,++x,y)
		   ],
		   getSectorAt:function(x,y){
			   for (var i=0;i<StarMap.sectors.length;i++){
				   var sector = StarMap.sectors[i];
				   if (sector.x == x && sector.y == y)
					   return sector;
			   }
		   }
};

/*
 * StarShip
 */

var StarShip={
	   sector:StarMap.sectors[0],
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
		update:function(sector){
			var element = ShortRangeScanScreen.element;
			element.empty();
			for (var y=0;y<10;y++){
				var tr = $("<tr></tr>");
				element.append(tr);
				for (var x=0;x<10;x++){
					content = "";
					for (var i=0;i<sector.stars.length;i++){
						var star = sector.stars[i];
						if (star.x == x && star.y == y)
							content+="*";
					}
					for (var i=0;i<sector.klingons.length;i++){
						var klingon = sector.klingons[i];
						if (klingon.x == x && klingon.y == y)
							content+="K";
					}
					for (var i=0;i<sector.starbases.length;i++){
						var starbase = sector.starbases[i];
						if (starbase.x == x && starbase.y == y)
							content+="!";
					}
					if (StarShip.sector === sector && StarShip.x == x && StarShip.y == y){
						content+="E";
					}
					if (content=="")
						content="&nbsp;";
					var td = $("<td id='q_"+x+"_"+y+"'><a href='#square_"+x+","+y+"'>"+content+"</td>");
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
			for (var i=0;i<StarMap.sectors.length;i++)
				LongRangeScanScreen.updateSector(StarMap.sectors[i]);
		},
		updateSector:function(sector){
			var e = sector.element;
			e.html("<a href='#sector_"+sector.x+","+sector.y+"'>"+sector.klingons.length+" "+sector.starbases.length+" "+sector.stars.length+"</a>");
			e.removeClass("has-starship");
			if (StarShip.sector == sector)
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
			ShortRangeScanScreen.update(StarShip.sector);
		}
};
/**
 * Controller
 */
var Controller={
		square:{x:0,y:0},
		resetCommands:function(){
			Tools.removePageCss("square-selection");
			Tools.removePageCss("phaser-selection");
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
			if (/square_\d,\d/.test(token)){
				var position = Tools.extractPositionFrom(token);
				Controller.onSquareSelected(position.x,position.y);
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
			if (false){
				var pos = Tools.extractPositionFrom(token);
				var sector = StarMap.getSectorAt(pos.x, pos.y);
				Controller.onSectorSelected(sector);
			}
		},
		onSquareSelected:function(x,y){
			Controller.resetCommands();
			Tools.addPageCss("square-selection");
			Controller.square.x = x;
			Controller.square.y = y;
		},
		onSectorSelected:function(sector){
		},
		shortRangeScan:function(){
			Tools.changeHash("short-range-scan");
			ShortRangeScanScreen.show(StarShip.sector);
		},
		longRangeScan:function(){
			Tools.changeHash("long-range-scan");
			LongRangeScanScreen.show();
		},
		onSectorSelected:function(sector){
			console.log(sector);
		},
		showComputerScreen:function(){
			Tools.changeHash("computer");
			Computer.show();
		},
		showStartScreen:function(){
			Controller.resetCommands();
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
			StarShip.x = Controller.square.x;
			StarShip.y = Controller.square.y;
			Controller.endRound();
		},
		firePhasers:function(strength){
			console.log("fired phasers with "+strength+" at "+Controller.square.x+","+Controller.square.y);
			Controller.endRound();
		},
		fireTorpedos:function(){
			console.log("fired torpedo");
			Controller.endRound();
		},
		selectWarpDestination:function(){
			Controller.resetCommands();
			Controller.longRangeScan();
		}
};

$.History.bind(function(state){
	Controller.onHistoryChanged(state);
});

Controller.startGame();


