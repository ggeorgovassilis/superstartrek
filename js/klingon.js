/**
 * Klingon AI
 */

var Klingons = {
	play : function(klingon, quadrant) {
		if (StarShip.quadrant != quadrant)
			return;
		// can raider fire on us?
		var distance = Tools.distance(klingon.x, klingon.y, StarShip.x,
				StarShip.y);
		var obstacle = Tools.findObstruction(quadrant, klingon.x, klingon.y,
				StarShip.x, StarShip.y);
		console.log("Klingon distance to us is", distance);
		if (distance > Constants.DISRUPTOR_RANGE || obstacle)
			Klingons.manueverIntoFiringPosition(klingon, StarShip.quadrant);
		else
			Klingons.fireOnStarship(klingon);
	},
	on_enterprise_warped : function() {
		var klingons = StarShip.quadrant.klingons;
		for (var i = 0; i < klingons.length; i++)
			Klingons.moveRandomly(klingons[i]);
	},
	moveRandomly : function(klingon) {
		var x = Math.floor(Math.random() * 8);
		var y = Math.floor(Math.random() * 8);
		var obstacle = StarMap.getAnythingInQuadrantAt(klingon.quadrant, x, y);
		if (obstacle)
			return;
		klingon.x = x;
		klingon.y = y;
		$window.trigger("klingon_moved");
	},
	manueverIntoFiringPosition : function(klingon, quadrant) {
		// find a spot which:
		// 1. is empty
		// 2. raider has a clear shot at us
		// 3. raider can move to unobstructed
		// 4. isn't more than 2 squares from original
		console.log("Moving Klingon into firing position");
		var initialDistanceToEnterprise = Tools.distance(klingon.x, klingon.y,
				StarShip.x, StarShip.y);
		var bestX = klingon.x;
		var bestY = klingon.y;
		var bestDistance = 10;

		for (var x = 0; x < 8; x++)
			for (var y = 0; y < 8; y++) {
				if (x == klingon.x && y == klingon.y)
					continue;
				if (Tools.distance(x, y, klingon.x, klingon.y) > Constants.KLINGON_IMPULSE_SPEED)
					continue;
				if (Tools.distance(StarShip.x, StarShip.y, x, y) >= initialDistanceToEnterprise)
					continue;
				var thing = StarMap.getAnythingInQuadrantAt(quadrant, x, y);
				if (thing)
					continue;
				var obstacle = Tools.findObstruction(quadrant, x, y,
						StarShip.x, StarShip.y);
				if (obstacle)
					continue;
				obstacle = Tools.findObstruction(quadrant, klingon.x,
						klingon.y, x, y);
				if (obstacle)
					continue;
				var distanceToEnterprise = Tools.distance(x, y, StarShip.x,
						StarShip.y);
				if (distanceToEnterprise < bestDistance) {
					bestDistance = distanceToEnterprise;
					bestX = x;
					bestY = y;
				}
			}
		klingon.x = bestX;
		klingon.y = bestY;
		$window.trigger("ship_moved");
		return;
	},
	fireOnStarship : function(klingon) {
		StarShip.shields = StarShip.shields - klingon.weaponPower;
		StarShip.maxShields = StarShip.maxShields * 4 / 5;
		StarShip.shields = Math.min(StarShip.shields, StarShip.maxShields);
		if (StarShip.shields < 0) {
			return IO.gameOverMessage("Klingon ship destroyed us, game over.");
		}
		return IO.message(function() {
		}, "Klingon ship fired at us, shields dropped to "
				+ Math.round(StarShip.shields));
	},
	damage : function(klingon, damage) {
		console.log("Assign damage to klingon", damage);
		klingon.shields -= damage;
		if (klingon.shields > 0) {
			$window.trigger("klingon_damaged", [ klingon ]);
		} else {
			Klingons.destroy(klingon);
		}
	},
	destroy : function(klingon) {
		klingon.quadrant.klingons.remove(klingon);
		$window.trigger("klingon_destroyed", [ klingon ])
	},
	on_klingon_damaged : function() {
		IO.message(null, "Target hit");
	},
	on_klingon_destroyed : function(event, klingon) {
		IO.message(null, "Target destroyed");
	}
};
$(window).on("enerprise_warped", Klingons.on_enterprise_warped);
$(window).on("klingon_damaged", Klingons.on_klingon_damaged);
$(window).on("klingon_destroyed", Klingons.on_klingon_destroyed);
