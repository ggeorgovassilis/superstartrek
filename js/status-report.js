var StatusReport = {
	energy : $("#report_energy"),
	energyConsumption : $("#report_energy_consumption"),
	torpedos : $("#report_torpedos"),
	location : $("#report_location"),
	shields : $("#report_shields"),
	stardate : $("#report_stardate"),
	reactor : $("#report_reactor"),
	reactorRemaining : $("#report_reactor_remaining"),
	maxImpulse : $("#report_max_impulse"),
	klingonsCount : $("#report_klingons_count"),
	update : function() {
		StatusReport.energy.text(StarShip.energy);
		StatusReport.energyConsumption
				.text(Computer.calculateBaseEnergyConsumption);
		StatusReport.torpedos.text(StarShip.torpedos);
		StatusReport.location.text(StarShip.quadrant.regionName + " "
				+ StarShip.quadrant.x + "," + StarShip.quadrant.y);
		StatusReport.shields.text(StarShip.shields + " / "
				+ StarShip.maxShields);
		StatusReport.stardate.text(Tools.formatStardate(Computer.stardate));
		StatusReport.reactor.text("%" + 100
				* (StarShip.reactorOutput / Constants.MAX_REACTOR_OUTPUT));
		StatusReport.reactorRemaining.text(StarShip.budget);
		StatusReport.klingonsCount.text(StarMap.countKlingons());
		StatusReport.maxImpulse.text("%"
				+ Math.round(100 * StarShip.maxImpulse
						/ Constants.MAX_IMPULSE_SPEED));
	}
};
