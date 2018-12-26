Controller.firePhasers = function() {
	var distance = Tools.distance(Enterprise.x, Enterprise.y, Controller.sector.x, Controller.sector.y);
	if (Math.floor(distance)>Constants.PHASER_RANGE)
		return IO.message(Controller.showSectorSelectionMenu,
				"Target is out of range");
	var strength = Enterprise.phaserPower;
	var klingon = StarMap.getKlingonInQuadrantAt(Enterprise.quadrant,
			Controller.sector.x, Controller.sector.y);
	if (!klingon) {
		return IO.message(Controller.showSectorSelectionMenu,
				"No Klingon at that sector");
	}
	if (Math.floor(distance)>Constants.PHASER_RANGE)
		return IO.message(Controller.showSectorSelectionMenu,
				"Target is out of range");
	var consumption = Computer.calculateEnergyConsumptionForPhasers(strength);
	if (!Computer.hasEnergyBudgetFor(consumption))
		return IO.message("Insufficient energy");
	Computer.consume(consumption);
	var damage = strength
			/ Tools.distance(Enterprise.x, Enterprise.y, klingon.x, klingon.y);
	$window.trigger("fired");
	Klingons.damage(klingon, damage);
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
