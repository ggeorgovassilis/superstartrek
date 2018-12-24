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
		ShortRangeScan.update(StarShip.quadrant);
	},
	something_changed : function() {
		ShortRangeScan.update(StarShip.quadrant);
	},
	update : function(quadrant) {
		if (ShortRangeScan.constructUi != null) {
			ShortRangeScan.constructUi();
			ShortRangeScan.constructUi = null;
		}
		ShortRangeScan.element.find("td").html("&nbsp;");
		ShortRangeScan.updateList("&nbsp;*&nbsp;", quadrant.stars, function(
				star) {
			return "star";
		});
		ShortRangeScan.updateList("o-}", quadrant.klingons, function(klingon) {
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
		if (StarShip.quadrant === quadrant)
			ShortRangeScan.updateList("O=Ξ", [ StarShip ], function(starhip) {
				return "";
			});
		$("#quadrant_name").text(StarShip.quadrant.regionName);
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

$(window).on("ship_moved", ShortRangeScan.something_changed);
$(window).on("init", ShortRangeScan.init);
$(window).on("fired", ShortRangeScan.something_changed);
$(window).on("klingon_destroyed", ShortRangeScan.something_changed);
$(window).on("klingon_damaged", ShortRangeScan.something_changed);
