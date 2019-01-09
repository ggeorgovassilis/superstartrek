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
	Enterprise.fireTorpedosAt(Controller.sector.x,Controller.sector.y);
};
