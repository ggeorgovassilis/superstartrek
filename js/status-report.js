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
	//also used in computer
	statusColor:function(part,total,e){
		var p = part/total;
		var c = "";
		if (p>0.99)
			c="ok";
		else if (p>0.5)
			c="damaged";
		else if (p>0.1)
			c="critical";
		else
			c="offline";
		e.removeClass("ok damaged critical offline");
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
		Tools.setElementText(StatusReport.energy, Enterprise.energy);
		Tools.setElementText(StatusReport.energyConsumption, Computer.calculateBaseEnergyConsumption());
		Tools.setElementText(StatusReport.torpedos, Enterprise.torpedos+" | "+(Enterprise.torpedosOnline?"ONLINE":"OFFLINE"));
		Tools.setElementText(StatusReport.location, Enterprise.quadrant.regionName + " "
				+ Enterprise.quadrant.x + "," + Enterprise.quadrant.y);
		Tools.setElementText(StatusReport.shields, Math.floor(Enterprise.shields) + " / "
				+ Math.floor(Enterprise.maxShields));
		Tools.setElementText(StatusReport.phaserPower, StatusReport.perc(Enterprise.phaserPower,Constants.ENTERPRISE_MAX_PHASER_POWER));
		Tools.setElementText(StatusReport.stardate, Tools.formatStardate(Computer.stardate));
		Tools.setElementText(StatusReport.reactor, "%" + 100
				* Math.floor(Enterprise.reactorOutput / Constants.MAX_REACTOR_OUTPUT));
		Tools.setElementText(StatusReport.reactorRemaining, Enterprise.budget);
		Tools.setElementText(StatusReport.klingonsCount, StarMap.countKlingons());
		Tools.setElementText(StatusReport.tacticalComputer, Enterprise.tacticalComputerOnline?"ONLINE":"OFFLINE");
		Tools.setElementText(StatusReport.maxImpulse, "%"
				+ Math.round(100 * Enterprise.maxImpulse
						/ Constants.MAX_IMPULSE_SPEED));
		Tools.setElementText(StatusReport.lrs, Enterprise.lrsOnline?"ONLINE":"OFFLINE");
	},
	legend:function(what,status){
		Tools.setElementText($("#what"), what);
		Tools.setElementText($("#status"), status);
	}
};
