Controller.firePhasers = function() {
	if (Enterprise.phaserPower<(Constants.ENTERPRISE_MAX_PHASER_POWER/4))
		return IO.message("Phasers array is offline").then.nothing();
	return Enterprise.firePhasersAt(Controller.sector.x, Controller.sector.y);
};

Controller.fireTorpedos = function() {
	if (Enterprise.torpedos < 1) {
		return IO.message("Out of torpedos").then.SRS();
	}
	if (!Enterprise.torpedosOnline){
		return IO.message("Torpedo bay damaged, cannot execute command.").then.SRS();
	}
	if (Enterprise.x === Controller.sector.x
			&& Enterprise.y === Controller.sector.y) {
		return IO.message("Cannot fire at self").then.SRS();
	}
	var obstacle = Tools.findObstruction(Enterprise.quadrant, Enterprise.x,
			Enterprise.y, Controller.sector.x, Controller.sector.y);
	Enterprise.torpedos--;
	Events.trigger(Events.WEAPON_FIRED,{weapon:"torpedo"});
	if (obstacle) {
		var thing = obstacle.obstacle;
		if (thing.star) {
			return IO.message("Photon torpedo hit star at "
					+ thing.x + "," + thing.y).then.endTurn();
		}
		if (thing.starbase) {
			Enterprise.quadrant.starbases.remove(thing);
			return IO.message("Photon torpedo hit starbase at "
					+ thing.x + "," + thing.y).then.endTurn();
		}
		if (thing.klingon) {
			var klingon = thing;
			var chance = 1 / Math.log(1 + Tools.distance(Enterprise.x,
					Enterprise.y, klingon.x, klingon.y));
			console.log("chance", chance, klingon);
			if (Math.random() <= chance) {
				//photon torpedos are inefficient against shields; damage malus for full shields
				var damage = Constants.MAX_TORPEDO_DAMAGE*(1-0.9*(Math.sqr(klingon.shields/klingon.maxShields)));
				Klingons.damage(klingon, damage);
				return IO.endTurn();
			} else{
				IO.message("Photon torpedo missed target").then.endTurn();
			}
		}
	} else
		return IO.message("Photon torpedo exploded in the void.").then.endTurn();
};
