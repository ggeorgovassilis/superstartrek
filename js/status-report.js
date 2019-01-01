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
	onKeyPressed:function(e){
		Controller.showComputerScreen();
	},
	perc:function(part,total){
		return "%"+Math.floor(100*part/total);
	},
	statusColor:function(part,total,e){
		var p = part/total;
		var c = "";
		if (p>0.9)
			c="ok";
		else
		if (p>0.5)
			c="damaged";
		else
			c="offline";
		e.addClass(c);
	},
	updateSchematics:function(){
		var schematics = $("#enterprise-schematics");
		schematics.attr("class","");
		StatusReport.statusColor(Enterprise.maxWarpSpeed,Constants.ENTERPRISE_MAX_WARP_SPEED, schematics.find(".warp"));
		StatusReport.statusColor(Enterprise.maxShields,Constants.ENTERPRISE_MAX_SHIELDS, schematics.find(".shields"));
		StatusReport.statusColor(Enterprise.phaserPower,Constants.ENTERPRISE_MAX_PHASER_POWER, schematics.find(".phasers"));
		StatusReport.statusColor(Enterprise.maxImpulse,Constants.MAX_IMPULSE_SPEED, schematics.find(".impulse"));
		StatusReport.statusColor(Enterprise.torpedosOnline?1:0,1, schematics.find(".torpedobay"));
		StatusReport.statusColor(Enterprise.lrsOnline?1:0,1, schematics.find(".scanners"));
	},
	update : function() {
		StatusReport.updateSchematics();
		StatusReport.energy.text(Enterprise.energy);
		StatusReport.energyConsumption
				.text(Computer.calculateBaseEnergyConsumption);
		StatusReport.torpedos.text(Enterprise.torpedos+" | "+(Enterprise.torpedosOnline?"ONLINE":"OFFLINE"));
		StatusReport.location.text(Enterprise.quadrant.regionName + " "
				+ Enterprise.quadrant.x + "," + Enterprise.quadrant.y);
		StatusReport.shields.text(Math.floor(Enterprise.shields) + " / "
				+ Math.floor(Enterprise.maxShields));
		StatusReport.phaserPower.text(StatusReport.perc(Enterprise.phaserPower,Constants.ENTERPRISE_MAX_PHASER_POWER));
		StatusReport.stardate.text(Tools.formatStardate(Computer.stardate));
		StatusReport.reactor.text("%" + 100
				* Math.floor(Enterprise.reactorOutput / Constants.MAX_REACTOR_OUTPUT));
		StatusReport.reactorRemaining.text(Enterprise.budget);
		StatusReport.klingonsCount.text(StarMap.countKlingons());
		StatusReport.tacticalComputer.text(Enterprise.tacticalComputerOnline?"ONLINE":"OFFLINE");
		StatusReport.maxImpulse.text("%"
				+ Math.round(100 * Enterprise.maxImpulse
						/ Constants.MAX_IMPULSE_SPEED));
		StatusReport.lrs.text(Enterprise.lrsOnline?"ONLINE":"OFFLINE");
	},
	legend:function(what,status){
		$("#what").text(what);
		$("#status").text(status);
	}
};
