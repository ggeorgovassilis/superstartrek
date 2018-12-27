/**
 * Quadrant scan
 */
var ShortRangeScan = {
	element : $("#quadrantscan"),
	updateList : function(symbol, list, formatter) {
		for (var i = 0; i < list.length; i++) {
			var thing = list[i];
			var tile = $("#cmd_selectSector_" + thing.x + "_" + thing.y);
			var css = formatter(thing);
			tile.attr("class", css);
			tile.html(symbol);
		}
	},
	init : function() {
		console.log("ShortRangeScan.init");
		ShortRangeScan.updateMap();
	},
	updateMap : function() {
		var quadrant = Enterprise.quadrant;
		if (ShortRangeScan.constructUi != null) {
			ShortRangeScan.constructUi();
			ShortRangeScan.constructUi = null;
		}
		ShortRangeScan.element.find("td").html("&nbsp;");
		ShortRangeScan.updateList("&nbsp;*&nbsp;", quadrant.stars, function(
				star) {
			return "star";
		});
		ShortRangeScan.updateList("c-}", quadrant.klingons, function(klingon) {
			if (klingon.shields < 25)
				return "damage-bad";
			if (klingon.shields < 50)
				return "damage-medium";
			if (klingon.shields < 75)
				return "damage-light";
			return "";
		});
		ShortRangeScan.updateList("&lt;!&gt;", quadrant.starbases, function(
				starbase) {
			return "";
		});
		if (Enterprise.quadrant === quadrant)
			ShortRangeScan.updateList("O=Ξ", [ Enterprise ], function(starhip) {
				return "";
			});
		$("#quadrant_name").text(Enterprise.quadrant.regionName);
		$("#srs_heading").removeClass("red-alert");
		if (!Enterprise.quadrant.klingons.isEmpty()){
			$("#srs_heading").addClass("red-alert");
		}
	},
	constructUi : function() {
		var element = ShortRangeScan.element;
		for (var y = 0; y < 8; y++) {
			var tr = $("<tr></tr>");
			element.append(tr);
			for (var x = 0; x < 8; x++) {
				var td = $("<td id='cmd_selectSector_" + x + "_" + y
						+ "'>&nbsp;</td>");
				tr.append(td);
			}
		}
	},
	selectSectorAt : function(x, y) {
		$("#quadrantscan .selected").removeClass("selected");
		$("#cmd_selectSector_" + x + "_" + y).addClass("selected");
	}
};

var ShortRangeScanScreen = {
	elem : $("#shortrangescan"),
	update : function(quadrant) {
		var index = 0;
		var qx = quadrant.x;
		var qy = quadrant.y;
		var cells = $("#shortrangescan td");
		for (var y = qy - 1; y <= qy + 1; y++)
			for (var x = qx - 1; x <= qx + 1; x++) {
				var cell = $(cells[index]);
				cell.removeClass("has-starbase");
				cell.removeClass("has-klingons");
				cell.removeClass("explored");
				if (x >= 0 && x <= 7 && y >= 0 && y <= 7) {
					var quadrant = StarMap.getQuadrantAt(x, y);
					quadrant.explored = true;
					LongRangeScanScreen.updateElementWithQuadrant(quadrant,
							cell);
				} else {
					cell.text("0");
					cell.attr("id", null);
				}
				index++;
			}
	}
};
Events.on(Events.START_GAME, ShortRangeScan.init);

Events.on(Events.ENTERPRISE_MOVED, ShortRangeScan.updateMap);
Events.on(Events.ENTERPRISE_WARPED, ShortRangeScan.updateMap);
Events.on(Events.WEAPON_FIRED, ShortRangeScan.updateMap);
//Events.on(Events.SETTINGS_CHANGED, ShortRangeScan.something_changed);
//Events.on(Events.ENTERPRISE_DAMAGED, ShortRangeScan.something_changed);

Events.on(Events.KLINGON_MOVED, ShortRangeScan.updateMap);
Events.on(Events.KLINGON_DESTROYED, ShortRangeScan.updateMap);
Events.on(Events.KLINGON_DAMAGED, ShortRangeScan.updateMap);
