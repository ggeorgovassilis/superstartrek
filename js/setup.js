var Setup = {
	makeQuadrant : function(regionName, x, y) {
		var quadrant = {
			regionName : regionName,
			x : x,
			y : y,
			element : $("#cmd_selectQuadrant_" + x + "_" + y),
			stars : Setup.makeStars(),
			starbases : [],
			explored : false
		};
		quadrant.klingons = Setup.makeKlingons(quadrant);

		quadrant.explored = quadrant.starbases.length > 0;
		return quadrant;
	},
	makeStars : function() {
		var a = new Array();
		for (var i = Tools.random(20); i > 0; i--)
			a.pushUnique({
				x : Math.round(Math.random() * 7),
				y : Math.round(Math.random() * 7),
				star : true,
				name : "a star"
			});
		return a;
	},
	makeKlingons : function(quadrant) {
		var a = new Array();
		var hasKlingon = Constants.CHANCE_OF_KLINGON_IN_QUADRANT > Math.random();
		if (hasKlingon) {
				var howMany = Math.max(1,Tools.random(Constants.MAX_KLINGONS_IN_QUADRANT));
				while (howMany--){
					//make lower types more likely
					var shipTypeIndex = Math.floor(Math.sqr(Math.random()) * Constants.KLINGON_SHIP_CLASS_MODIFIERS.length);
					var shipType = Constants.KLINGON_SHIP_CLASS_MODIFIERS[shipTypeIndex];
					a.pushUnique({
						x : Math.round(Math.random() * 7),
						y : Math.round(Math.random() * 7),
						shields : Constants.MAX_KLINGON_SHIELD*shipType.modifier,
						maxShields : Constants.MAX_KLINGON_SHIELD*shipType.modifier,
						weaponPower : Constants.KLINGON_DISRUPTOR_POWER*shipType.modifier,
						klingon : true,
						symbol:shipType.symbol,
						quadrant : quadrant,
						name : shipType.name
					});
				}
		}
		return a;
	},
	makeStarMap:function(){
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
			Setup.makeStarbases();

	},
	makeStarbases : function() {
		for (var i=0;i<Constants.STARBASES_ON_MAP;i++){
			var quadrant = StarMap.quadrants[i*22];
			var starbase = {
					x : Math.round(Math.random() * 7),
					y : Math.round(Math.random() * 7),
					starbase : true,
					name : "a federation starbase"
				};
			quadrant.starbases.pushUnique(starbase);
			for (var x=Math.max(0,quadrant.x-1);x<=Math.min(7,quadrant.x+1);x++)
			for (var y=Math.max(0,quadrant.y-1);y<=Math.min(7,quadrant.y+1);y++)
				StarMap.getQuadrantAt(x,y).explored=true;
		}
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
		$("#loading").remove();
	}

}