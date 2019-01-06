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
		return IO.message("Insufficient reactor output").then.SRS();
	Computer.consume(consumption);
	Enterprise.x = finalX;
	Enterprise.y = finalY;
	Computer.advanceClock(Constants.DURATION_OF_MOVEMENT_PER_SECTOR * distance);
	Events.trigger(Events.ENTERPRISE_MOVED);
	Controller.endTurn();
};
Controller.warpTo = function(quadrant) {
	var distance = Tools.distance(Enterprise.quadrant.x, Enterprise.quadrant.y,
			quadrant.x, quadrant.y);
	if (distance === 0)
		return Controller.showComputerScreen();
	//TODO: rework warp. speeds are quadratic(?). Any distance should be possible but impact time accordingly.
	if (distance>Enterprise.maxWarpSpeed)
		return IO.message("That course exceeds maximum warp").then.SRS();
	var consumption = Computer.calculateEnergyConsumptionForWarpDrive(
			Enterprise.quadrant, quadrant);
	var forceStopAtQuadrant = null;
	// distance of 1 cannot be intercepted, otherwise Enterprise could not escape a quadrant
	if (distance>=2)
	Tools.walkLine(Enterprise.quadrant.x, Enterprise.quadrant.y, quadrant.x, quadrant.y, function(x,y){
		var q = StarMap.getQuadrantAt(x,y);
		if (!q.klingons.isEmpty()){
			forceStopAtQuadrant = q;
			IO.message("We were intercepted by "+q.klingons[0].name).message("Dropping out of warp at "+q.regionName);
		}
		return forceStopAtQuadrant==null;
	});
	if (forceStopAtQuadrant)
		quadrant = forceStopAtQuadrant;
	distance = Tools.distance(Enterprise.quadrant.x, Enterprise.quadrant.y,
			quadrant.x, quadrant.y);
	
	var speed = Math.min(distance, Constants.MAX_WARP_SPEED);
	var turns = Constants.DURATION_OF_MOVEMENT_PER_QUADRANT * distance / speed;
	Computer.consume(consumption);
	Computer.advanceClock(turns);
	Enterprise.quadrant = quadrant;
	Enterprise.repositionIfSectorOccupied();
	Events.trigger(Events.ENTERPRISE_WARPED);
	Controller.endTurn();
};

Events.on(Events.QUADRANT_SELECTED, Controller.warpTo);

