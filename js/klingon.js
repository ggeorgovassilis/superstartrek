/**
 * Klingon AI
 */

var Klingons = {
	play : function(klingon, quadrant) {
		if (Enterprise.quadrant != quadrant)
			return;
		// can raider fire on us?
		var distance = Tools.distance(klingon.x, klingon.y, Enterprise.x,
				Enterprise.y);
		var obstacle = Tools.findObstruction(quadrant, klingon.x, klingon.y,
				Enterprise.x, Enterprise.y);
		console.log("Klingon distance to us is", distance);
		if (distance > Constants.DISRUPTOR_RANGE || obstacle)
			Klingons.manueverIntoFiringPosition(klingon, Enterprise.quadrant);
		else
			Klingons.fireOnEnterprise(klingon);
	},
	on_enterprise_warped : function() {
		console.log("Enterprise dropping out of warp")
		var klingons = Enterprise.quadrant.klingons;
		for (var i = 0; i < klingons.length; i++)
			Klingons.moveRandomly(klingons[i]);
	},
	moveRandomly : function(klingon) {
		console.log("moving randomly ",klingon)
		var x = Math.floor(Math.random() * 8);
		var y = Math.floor(Math.random() * 8);
		var obstacle = StarMap.getAnythingInQuadrantAt(klingon.quadrant, x, y);
		if (obstacle)
			return;
		klingon.x = x;
		klingon.y = y;
		Events.trigger(Events.KLINGON_MOVED,{target:klingon});
	},
	manueverIntoFiringPosition : function(klingon, quadrant) {
		// find a spot which:
		// 1. is empty
		// 2. raider has a clear shot at us
		// 3. raider can move to unobstructed
		// 4. isn't more than 2 squares from original
		console.log("Moving Klingon into firing position");
		var initialDistanceToEnterprise = Tools.distance(klingon.x, klingon.y,
				Enterprise.x, Enterprise.y);
		var bestX = klingon.x;
		var bestY = klingon.y;
		var bestDistance = 10;

		for (var x = 0; x < 8; x++)
			for (var y = 0; y < 8; y++) {
				if (x == klingon.x && y == klingon.y)
					continue;
				if (Tools.distance(x, y, klingon.x, klingon.y) > Constants.KLINGON_IMPULSE_SPEED)
					continue;
				if (Tools.distance(Enterprise.x, Enterprise.y, x, y) >= initialDistanceToEnterprise)
					continue;
				var thing = StarMap.getAnythingInQuadrantAt(quadrant, x, y);
				if (thing)
					continue;
				var obstacle = Tools.findObstruction(quadrant, x, y,
						Enterprise.x, Enterprise.y);
				if (obstacle)
					continue;
				obstacle = Tools.findObstruction(quadrant, klingon.x,
						klingon.y, x, y);
				if (obstacle)
					continue;
				var distanceToEnterprise = Tools.distance(x, y, Enterprise.x,
						Enterprise.y);
				if (distanceToEnterprise < bestDistance) {
					bestDistance = distanceToEnterprise;
					bestX = x;
					bestY = y;
				}
			}
		klingon.x = bestX;
		klingon.y = bestY;
		Events.trigger(Events.ENTERPRISE_MOVED);
		return;
	},
	fireOnEnterprise : function(klingon) {
		return Enterprise.assignDamage(klingon.weaponPower);
	},
	damage : function(klingon, damage) {
		console.log("Assign damage to klingon", damage);
		klingon.shields -= damage;
		if (klingon.shields > 0) {
			Events.trigger(Events.KLINGON_DAMAGED,{target:klingon})
		} else {
			Klingons.destroy(klingon);
		}
	},
	destroy : function(klingon) {
		klingon.quadrant.klingons.remove(klingon);
		Events.trigger(Events.KLINGON_DESTROYED, {target:klingon})
	},
	on_klingon_damaged : function() {
		IO.message("Target hit");
	},
	on_klingon_destroyed : function(event, klingon) {
		IO.message("Target destroyed");
	}
};
Events.on(Events.ENTERPRISE_WARPED, Klingons.on_enterprise_warped);
Events.on(Events.KLINGON_DAMAGED, Klingons.on_klingon_damaged);
Events.on(Events.KLINGON_DESTROYED, Klingons.on_klingon_destroyed);
