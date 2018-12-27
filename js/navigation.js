Controller.navigate = function() {
	var finalX = Controller.sector.x;
	var finalY = Controller.sector.y;
	var obstacle = Tools.findObstruction(Enterprise.quadrant, Enterprise.x,
			Enterprise.y, Controller.sector.x, Controller.sector.y);
	if (obstacle) {
		finalX = obstacle.x;
		finalY = obstacle.y;
	}
	// movement obstructed?
	distance = Tools.distance(Enterprise.x, Enterprise.y, finalX, finalY);
	if (distance === 0)
		return Controller.showComputerScreen();
	var maxSpeed = Enterprise.maxImpulse;
	if (distance > maxSpeed) {
		return IO.message("Command exceeds maximum impulse speed " + maxSpeed).then.SRS();
	}
	var consumption = Computer.calculateEnergyConsumptionForMovement(distance);
	if (!Computer.hasEnergyBudgetFor(consumption))
		return;
	Computer.consume(consumption);
	Enterprise.x = finalX;
	Enterprise.y = finalY;
	Computer.advanceClock(Constants.DURATION_OF_MOVEMENT_PER_SECTOR * distance);
	Events.trigger(Events.ENTERPRISE_MOVED);
	Controller.endTurn();
};
Controller.selectWarpDestination = function() {
	Controller.longRangeScan();
};
Controller.warpTo = function(quadrant) {
	var distance = Tools.distance(Enterprise.quadrant.x, Enterprise.quadrant.y,
			quadrant.x, quadrant.y);
	if (distance === 0)
		return Controller.showComputerScreen();
	var consumption = Computer.calculateEnergyConsumptionForWarpDrive(
			Enterprise.quadrant, quadrant);
	var speed = Math.min(distance, Constants.MAX_WARP_SPEED);
	var turns = Constants.DURATION_OF_MOVEMENT_PER_QUADRANT * distance / speed;
	Computer.consume(consumption);
	Computer.advanceClock(turns);
	Enterprise.quadrant = quadrant;
	Enterprise.repositionIfSectorOccupied();
	Events.trigger(Events.ENTERPRISE_WARPED);
	Controller.endTurn();
};
Controller.dockWithStarbase = function() {
}
