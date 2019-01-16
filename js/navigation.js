var Navigation={
computeChanceOfEscape:function(Enterprise,targetQuadrant){
	var sourceQuadrant = Enterprise.quadrant;
	var obstacles = 0;
	var smallestDistanceToKlingon = 1000;
	sourceQuadrant.klingons.foreach(function(k){
		if (k.disruptorsOnline || k.enginesOnline){
			obstacles++;
			var d = Tools.distance(Enterprise.x,Enterprise.y,k.x,k.y);
			smallestDistanceToKlingon = Math.min(d,smallestDistanceToKlingon);
		}
	});
	if (obstacles>0) // stars further obstruct warp only if there are active klingons
		obstacles+=Enterprise.quadrant.stars.length/2;
	if (smallestDistanceToKlingon<4) //being close to an active klingon reduces escape changes further
		obstacles+=1;
	var distanceFromCentre = Tools.distance(Enterprise.x,Enterprise.y,3.5,3.5);
	obstacles=Math.max(0,obstacles-0.5*distanceFromCentre);
	var chance = (1/(1+obstacles));
	console.log("escape chance",chance);
	return chance;
},
navigate:function(){
	var finalX = Controller.sector.x;
	var finalY = Controller.sector.y;
	var distance = Tools.distance(Enterprise.x, Enterprise.y, finalX, finalY);
	if (distance === 0)
		return Controller.showComputerScreen();
	var maxSpeed = Enterprise.maxImpulse;
	if (distance > maxSpeed) {
		return IO.message("Command exceeds maximum impulse speed " + maxSpeed).then.SRS();
	}
	var obstacle = Tools.findObstruction(Enterprise.quadrant, Enterprise.x,
			Enterprise.y, Controller.sector.x, Controller.sector.y);
	if (obstacle) {
		finalX = obstacle.x;
		finalY = obstacle.y;
		if (obstacle.klingon){
			Klingons.decloak(obstacle.klingon);
			IO.message(obstacle.name+" decloaked at "+obstacle.x+":"+obstacle.y);
		}
	};
	// movement obstructed?
	var consumption = Computer.calculateEnergyConsumptionForMovement(distance);
	if (!Computer.hasEnergyBudgetFor(consumption))
		return IO.message("Insufficient reactor output").then.SRS();
	Computer.consume(consumption);
	Enterprise.x = finalX;
	Enterprise.y = finalY;
	Computer.advanceClock(Constants.DURATION_OF_MOVEMENT_PER_SECTOR * distance);
	Events.trigger(Events.ENTERPRISE_MOVED);
	Controller.endTurn();
	},
	
warpTo:function(quadrant){
	var distance = Tools.distance(Enterprise.quadrant.x, Enterprise.quadrant.y, quadrant.x, quadrant.y);
	if (distance === 0)
		return Controller.showComputerScreen();
	//TODO: rework warp. speeds are quadratic(?). Any distance should be possible but impact time accordingly.
	if (distance>Enterprise.maxWarpSpeed)
		return IO.message("That course exceeds maximum warp").then.SRS();
	var consumption = Computer.calculateEnergyConsumptionForWarpDrive(Enterprise.quadrant, quadrant);
	var forceStopAtQuadrant = null;
	if (Math.random()>Navigation.computeChanceOfEscape(Enterprise, quadrant)){
		return IO.message("We can't find a clear path").then.endTurn();
	}
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
	distance = Tools.distance(Enterprise.quadrant.x, Enterprise.quadrant.y, quadrant.x, quadrant.y);
		
	var speed = Math.min(distance, Constants.MAX_WARP_SPEED);
	var turns = Constants.DURATION_OF_MOVEMENT_PER_QUADRANT * distance / speed;
	Computer.consume(consumption);
	Computer.advanceClock(turns);
	Enterprise.quadrant = quadrant;
	Enterprise.repositionIfSectorOccupied();
	Events.trigger(Events.ENTERPRISE_WARPED);
	Controller.endTurn();
	}
};

Controller.navigate = function() {
	Navigation.navigate();
};

Events.on(Events.QUADRANT_SELECTED, Navigation.warpTo);

