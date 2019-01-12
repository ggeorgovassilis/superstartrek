/**
 * Klingon AI
 */

var Klingons = {
	makeShip:function(shipType, quadrant){
		return {
			x : Math.round(Math.random() * 7),
			y : Math.round(Math.random() * 7),
			shields : Constants.MAX_KLINGON_SHIELD*shipType.modifier,
			maxShields : Constants.MAX_KLINGON_SHIELD*shipType.modifier,
			weaponPower : Constants.KLINGON_DISRUPTOR_POWER*shipType.modifier,
			klingon : true,
			enginesOnline: true,
			disruptorsOnline:true,
			symbol:shipType.symbol,
			quadrant : quadrant,
			cloaked:true,
			name : shipType.name
		};
	},
	play : function(klingon, quadrant) {
		if (Enterprise.quadrant != quadrant)
			return;
		var distance = Tools.distance(klingon.x, klingon.y, Enterprise.x,
				Enterprise.y);
		if (distance <= Constants.DISRUPTOR_RANGE){
			var obstacle = Tools.findObstruction(quadrant, klingon.x, klingon.y,
					Enterprise.x, Enterprise.y);
			if (!obstacle || obstacle.obstacle === Enterprise)
				return Klingons.fireOnEnterprise(klingon);
		}
		return Klingons.manueverIntoFiringPosition(klingon, Enterprise.quadrant);
	},
	decloak:function(klingon){
		if (klingon.cloaked){
			IO.message(klingon.name+" decloaked at "+klingon.x+":"+klingon.y);
			klingon.cloaked=false;
			Events.trigger(Events.KLINGON_MOVED,{target:klingon});
		}
	},
	resetKlingons:function(klingons){
		klingons.foreach(function(klingon){
			//can't cloaked if damaged too much
			klingon.cloaked=klingon.shields>klingon.maxShields/2;
			klingon.enginesOnline=true;
			klingon.disruptorsOnline=true;
			Klingons.moveRandomly(klingon);
		})
	},
	on_enterprise_warped : function() {
		Klingons.resetKlingons(Enterprise.quadrant.klingons);
	},
	moveRandomly : function(klingon) {
		var x = Math.floor(Math.random() * 8);
		var y = Math.floor(Math.random() * 8);
		var obstacle = StarMap.getAnythingInQuadrantAt(klingon.quadrant, x, y);
		if (obstacle)
			return;
		klingon.x = x;
		klingon.y = y;
		if (!klingon.cloaked)
			Events.trigger(Events.KLINGON_MOVED,{target:klingon});
	},
	manueverIntoFiringPosition : function(klingon, quadrant) {
		if (!klingon.enginesOnline)
			return;
		var path = Tools.findPathBetween(quadrant,klingon.x,klingon.y,Enterprise.x, Enterprise.y);
		if (!path || !path.length)
			return;
		var i = Math.min(Constants.KLINGON_IMPULSE_SPEED, path.length)-1;
		var lastPosition = path[i];
		klingon.x = lastPosition.x;
		klingon.y = lastPosition.y;
		if (!klingon.cloaked)
			Events.trigger(Events.KLINGON_MOVED);
		return;
	},
	fireOnEnterprise : function(klingon) {
		if (!klingon.disruptorsOnline)
			return;
		Klingons.decloak(klingon);
		return Enterprise.assignDamage(klingon.weaponPower,klingon);
	},
	assignDamage : function(klingon, damage) {
		klingon.shields -= damage;
		if (klingon.cloaked){
			Klingons.decloak(klingon);
			Klingons.destroy(klingon);
		} else if (klingon.shields > 0) {
			klingon.enginesOnline = klingon.enginesOnline && ((klingon.shields/klingon.maxShields)>=0.5*Math.random());
			klingon.disruptorsOnline = klingon.disruptorsOnline && ((klingon.shields/klingon.maxShields)>=0.5*Math.random());
			Events.trigger(Events.KLINGON_DAMAGED,{target:klingon})
		} else {
			Klingons.destroy(klingon);
		}
	},
	destroy : function(klingon) {
		klingon.shields = 0;
		Events.trigger(Events.KLINGON_DESTROYED, {target:klingon})
		klingon.quadrant.klingons.remove(klingon);
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
