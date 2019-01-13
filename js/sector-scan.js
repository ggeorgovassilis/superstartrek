var SectorScanner={
		scan:function(query){
			Tools.gotoScreen("sectorscan");
			console.log(query);
			var quadrant = query.quadrant;
			var x = query.x;
			var y = query.y;
			var thing = StarMap.getAnythingInQuadrantAt(quadrant,x,y);
			if (thing === Enterprise)
				return Tools.gotoScreen("statusreport");
			var name = "";
			var shields = "";
			var engines = "";
			var weapons = "";
			var type = "";
			if (!thing || thing.cloaked){
				name = "Nothing";
			} else if (thing.klingon){
				shields = "%"+Tools.perc(thing.shields,thing.maxShields);
				engines = thing.enginesOnline?"ONLINE":"OFFLINE";
				weapons = thing.disruptorsOnline?"ONLINE":"OFFLINE";
				type = "has-klingon";
				name = thing.name;
			} else if (thing.star)
				name = "A star";
			else if (thing.starbase)
				name = "A Federation star base";
			$("#object-name").text(name);
			$("#object-quadrant").text(quadrant.regionName);
			$("#object-location").text(x+":"+y);
			$("#screen-sectorscan").removeClass("has-klingon");
			$("#screen-sectorscan").addClass(type);
			$("#scan-report-engines").text(engines);
			$("#scan-report-weapons").text(weapons);
			$("#scan-report-shields").text(shields);
		}
}

Events.on(Events.SECTOR_SCAN,SectorScanner.scan);