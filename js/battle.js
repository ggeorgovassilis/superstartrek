Controller.firePhasers = function(setting) {
	var strength = setting*15; //1->15, 2->30, 3->45
	var klingon = StarMap.getKlingonInQuadrantAt(StarShip.quadrant,
			Controller.sector.x, Controller.sector.y);
	if (!klingon) {
		return IO.message(Controller.showSectorSelectionMenu,
				"No Klingon at that sector");
	}
	var consumption = Computer.calculateEnergyConsumptionForPhasers(strength);
	if (!Computer.hasEnergyBudgetFor(consumption))
		return IO.message("Insufficient energy");
	Computer.consume(consumption);
	var damage = strength
			/ Tools.distance(StarShip.x, StarShip.y, klingon.x, klingon.y);
	$window.trigger("fired");
	Klingons.damage(klingon, damage);
	return IO.endRound();
};

Controller.fireTorpedos = function() {
	if (StarShip.torpedos < 1) {
		return IO.message(Controller.showComputerScreen, "Out of torpedos");
	}
	if (StarShip.x === Controller.sector.x
			&& StarShip.y === Controller.sector.y) {
		return IO.message(Controller.fireTorpedos, "Cannot fire at self");
	}
	var obstacle = Tools.findObstruction(StarShip.quadrant, StarShip.x,
			StarShip.y, Controller.sector.x, Controller.sector.y);
	StarShip.torpedos--;
	$window.trigger("fired");
	if (obstacle) {
		var thing = obstacle.obstacle;
		if (thing.star) {
			return IO.messageAndEndRound("Photon torpedo hit star at "
					+ thing.x + "," + thing.y);
		}
		if (thing.starbase) {
			StarShip.quadrant.starbases.remove(thing);
			return IO.messageAndEndRound("Photon torpedo hit starbase at "
					+ thing.x + "," + thing.y);
		}
		if (thing.klingon) {
			var klingon = thing;
			var chance = 1 / Math.log(1 + Tools.distance(StarShip.x,
					StarShip.y, klingon.x, klingon.y));
			console.log("chance", chance, klingon);
			if (Math.random() <= chance) {
				//photon torpedos are inefficient against shields; damage malus for full shields
				var damage = Constants.MAX_TORPEDO_DAMAGE*(1.1-(klingon.shields/klingon.maxShields));
				Klingons.damage(klingon, damage);
				return IO.endRound();
			} else{
				IO.messageAndEndRound("Photon torpedo missed target");
			}
		}
	} else
		return IO.messageAndEndRound("Photon torpedo exploded in the void.");
};
