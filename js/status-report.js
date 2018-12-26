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
	phaserPower:$("#report_phaser_power"),
	tacticalComputer:$("#report_tactical_computer"),
	lrs:$("#report_LRS"),
	perc:function(part,total){
		return "%"+Math.floor(100*part/total);
	},
	update : function() {
		StatusReport.energy.text(Enterprise.energy);
		StatusReport.energyConsumption
				.text(Computer.calculateBaseEnergyConsumption);
		StatusReport.torpedos.text(Enterprise.torpedos+" | "+(Enterprise.torpedosOnline?"ONLINE":"OFFLINE"));
		StatusReport.location.text(Enterprise.quadrant.regionName + " "
				+ Enterprise.quadrant.x + "," + Enterprise.quadrant.y);
		StatusReport.shields.text(Enterprise.shields + " / "
				+ Enterprise.maxShields);
		StatusReport.phaserPower.text(StatusReport.perc(Enterprise.phaserPower,Constants.ENTERPRISE_MAX_PHASER_POWER));
		StatusReport.stardate.text(Tools.formatStardate(Computer.stardate));
		StatusReport.reactor.text("%" + 100
				* (Enterprise.reactorOutput / Constants.MAX_REACTOR_OUTPUT));
		StatusReport.reactorRemaining.text(Enterprise.budget);
		StatusReport.klingonsCount.text(StarMap.countKlingons());
		StatusReport.tacticalComputer.text(Enterprise.tacticalComputerOnline?"ONLINE":"OFFLINE");
		StatusReport.maxImpulse.text("%"
				+ Math.round(100 * Enterprise.maxImpulse
						/ Constants.MAX_IMPULSE_SPEED));
		StatusReport.lrs.text(Enterprise.lrsOnline?"ONLINE":"OFFLINE");
	}
};
