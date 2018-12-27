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
		var hasKlingon = true;
		while ((hasKlingon = (Constants.CHANCE_OF_KLINGON_IN_QUADRANT > Math
				.random()))) {
				a.pushUnique({
					x : Math.round(Math.random() * 7),
					y : Math.round(Math.random() * 7),
					shields : Constants.MAX_KLINGON_SHIELD,
					maxShields : Constants.MAX_KLINGON_SHIELD,
					weaponPower : Constants.KLINGON_DISRUPTOR_POWER,
					klingon : true,
					quadrant : quadrant,
					name : "a klingon raider"
				});
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
	}
}