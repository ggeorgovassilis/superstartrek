var Setup = {
	makeQuadrant : function(regionName, x, y) {
		var quadrant = {
			regionName : regionName,
			x : x,
			y : y,
			element : $("#cmd_selectQuadrant_" + x + "_" + y),
			stars : Setup.makeStars(),
			starbases : Setup.makeStarbases(),
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
				var howMany = Math.floor(1+Math.random()*Constants.MAX_KLINGONS_IN_QUADRANT);
				while (howMany--){
					//make lower types more likely
					var shipTypeIndex = Math.floor(Math.sqrt(Math.random()) * Constants.KLINGON_SHIP_CLASS_MODIFIERS.length);
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
	makeStarbases : function() {
		var a = [];
		var hasStarbase = Constants.CHANCE_OF_STARBASE_IN_QUADRANT > Math
				.random();
		if (hasStarbase)
			a.pushUnique({
				x : Math.round(Math.random() * 7),
				y : Math.round(Math.random() * 7),
				starbase : true,
				name : "a federation starbase"
			});
		return a;
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