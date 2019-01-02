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
		if (distance <= Constants.DISRUPTOR_RANGE && obstacle.obstacle===Enterprise)
			Klingons.fireOnEnterprise(klingon);
		else
			Klingons.manueverIntoFiringPosition(klingon, Enterprise.quadrant);
	},
	on_enterprise_warped : function() {
		var klingons = Enterprise.quadrant.klingons;
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
		Events.trigger(Events.KLINGON_MOVED,{target:klingon});
	},
	manueverIntoFiringPosition : function(klingon, quadrant) {
		var path = Tools.findPathBetween(quadrant,klingon.x,klingon.y,Enterprise.x, Enterprise.y);
		if (!path || !path.length)
			return;
		var i = Math.min(Constants.KLINGON_IMPULSE_SPEED, path.length)-1;
		var lastPosition = path[i];
		klingon.x = lastPosition.x;
		klingon.y = lastPosition.y;
		Events.trigger(Events.KLINGON_MOVED);
		return;
	},
	fireOnEnterprise : function(klingon) {
		return Enterprise.assignDamage(klingon.weaponPower,klingon);
	},
	damage : function(klingon, damage) {
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
