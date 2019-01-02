/**
 * Quadrant scan
 */
var ShortRangeScan = {
	element : $("#quadrantscan"),
	quadrantScanCells:[],
	updateList : function(symbol, list, formatter) {
		//function is called a lot, so going native JS
		list.foreach(function(thing){
			var tile = document.getElementById("cmd_selectSector_" + thing.x + "_" + thing.y);
			var css = formatter(thing);
			tile.setAttribute("class", css);
			tile.innerHTML = symbol;
		});
	},
	init : function() {
		ShortRangeScanScreen.init();
		ShortRangeScan.updateMap();
		var list = ShortRangeScan.element.find("td");
		for (var i=0;i<list.length;i++)
			ShortRangeScan.quadrantScanCells.push(list[i]);
	},
	clearCells:function(){
		var cells = ShortRangeScan.quadrantScanCells;
		var len = cells.length;
		while (len--)
			cells[len].textContent="";
		// this used to be:
		// ShortRangeScan.element.find("td").html("&nbsp;");
		// but that lags on mobile phones, so we're going native JS for speed
	},
	updateMapInner:function() {
		var quadrant = Enterprise.quadrant;
		ShortRangeScan.clearCells();
		ShortRangeScan.updateList("&nbsp;*&nbsp;", quadrant.stars, function(star) {
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
		if (!Enterprise.quadrant.klingons.isEmpty()){
			$("#srs_heading").addClass("red-alert");
		} else $("#srs_heading").removeClass("red-alert");

	},
	updateMap:function(){
		//profiling revealed that updateMap is called several times in the same interaction.
		//that's why I'm deferring it to a single call.
		ShortRangeScan.constructUi();
		ShortRangeScan.constructUi=nop; // avoid reconstructing UI every time this function is called
		if (ShortRangeScan.updatePending)
			return;
		ShortRangeScan.updatePending=setTimeout(function(){
			ShortRangeScan.updatePending=null;
			ShortRangeScan.updateMapInner();
		},10);
	},
	constructUi : function() {
		var element = ShortRangeScan.element;
		for (var y = 0; y < 8; y++) {
			var tr = $("<tr></tr>");
			for (var x = 0; x < 8; x++) {
				var td = $("<td id='cmd_selectSector_" + x + "_" + y
						+ "'>&nbsp;</td>");
				tr.append(td);
			}
			element.append(tr);
		}
		element.find("td").on("click", ShortRangeScan.onSectorClicked);
	},
	onSectorClicked:function(e){
		var cell = $(e.currentTarget);
		var id = cell.attr("id");
		var parts = /\w+_(\d)_(\d)/.exec(id);
		var x = parseInt(parts[1]);
		var y = parseInt(parts[2]);
		$("#quadrantscan .selected").removeClass("selected");
		cell.addClass("selected");
		Events.trigger(Events.SECTOR_SELECTED,{x:x,y:y});
		ShortRangeScan.positionSectorSelectionMenu(e.pageX,e.pageY);
	},
	positionSectorSelectionMenu:function(x,y){
		var eMenu = $("#sectorselectionbar");
		eMenu.offset({top:y,left:0});
	},
	onQuadrantClicked:function(e){
		var cell = $(e.currentTarget);
		console.log(cell);
		var id = cell.attr("id");
		if (id && id.startsWith("cmd")){
			var parts = /\w+_(\d)_(\d)/.exec(id);
			var x = parseInt(parts[1]);
			var y = parseInt(parts[2]);
			Events.trigger(Events.QUADRANT_SELECTED,StarMap.getQuadrantAt(x,y));
		}
	}
};

var ShortRangeScanScreen = {
	elem : $("#shortrangescan"),
	init:function(){
		$("#shortrangescan td").on("click", ShortRangeScan.onQuadrantClicked);
	},
	updateQuadrant : function(quadrant) {
		var index = 0;
		var qx = quadrant.x;
		var qy = quadrant.y;
		var cells = $("#shortrangescan td");
		for (var y = qy - 1; y <= qy + 1; y++)
			for (var x = qx - 1; x <= qx + 1; x++) {
				var cell = $(cells[index]);
				cell.attr("class","");
				if (x >= 0 && x <= 7 && y >= 0 && y <= 7) {
					var quadrant = StarMap.getQuadrantAt(x, y);
					quadrant.explored = true;
					LongRangeScanScreen.updateElementWithQuadrant(quadrant,cell);
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
