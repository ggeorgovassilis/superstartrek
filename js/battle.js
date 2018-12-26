
Controller.firePhasers = function() {
	if (Enterprise.phaserPower<(Constants.ENTERPRISE_MAX_PHASER_POWER/4))
		return IO.message(Controller.nop,"Phasers array is offline");
	Enterprise.firePhasersAt(Controller.sector.x, Controller.sector.y);
	return IO.endRound();
};

Controller.fireTorpedos = function() {
	if (Enterprise.torpedos < 1) {
		return IO.message(Controller.showComputerScreen, "Out of torpedos");
	}
	if (!Enterprise.torpedosOnline){
		return IO.message(Controller.showComputerScreen, "Torpedo bay damaged, cannot execute command.");
	}
	if (Enterprise.x === Controller.sector.x
			&& Enterprise.y === Controller.sector.y) {
		return IO.message(Controller.fireTorpedos, "Cannot fire at self");
	}
	var obstacle = Tools.findObstruction(Enterprise.quadrant, Enterprise.x,
			Enterprise.y, Controller.sector.x, Controller.sector.y);
	Enterprise.torpedos--;
	$window.trigger("fired");
	if (obstacle) {
		var thing = obstacle.obstacle;
		if (thing.star) {
			return IO.messageAndEndRound("Photon torpedo hit star at "
					+ thing.x + "," + thing.y);
		}
		if (thing.starbase) {
			Enterprise.quadrant.starbases.remove(thing);
			return IO.messageAndEndRound("Photon torpedo hit starbase at "
					+ thing.x + "," + thing.y);
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
				return IO.endRound();
			} else{
				IO.messageAndEndRound("Photon torpedo missed target");
			}
		}
	} else
		return IO.messageAndEndRound("Photon torpedo exploded in the void.");
};
