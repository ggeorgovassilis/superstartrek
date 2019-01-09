/**
 * Quadrant scan
 */
var ShortRangeScan = {
	element : $("#quadrantscan"),
	quadrantScanCells:[],
	updateList : function(list, formatter) {
		//function is called a lot, so going native JS
		list.foreach(function(thing){
			var tile = document.getElementById("cmd_selectSector_" + thing.x + "_" + thing.y);
			var format = formatter(thing);
			tile.setAttribute("class", format.css);
			tile.innerHTML = format.symbol;
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
		//normally the 'class' attribute should be also cleared, but 1) there are (currently) no cell CSS rules
		//which would make an empty cell look weird and 2) any new content would overwrite the CSS anyway
		while (len--)
			cells[len].textContent="";
		// this used to be:
		// ShortRangeScan.element.find("td").html("&nbsp;");
		// but that lags on mobile phones, so we're going native JS for speed
	},
	_updateMap:function() {
		var quadrant = Enterprise.quadrant;
		ShortRangeScan.clearCells();
		ShortRangeScan.updateList(quadrant.stars, function(star) {
			return {css:"star",symbol:"&nbsp;*&nbsp;"};
		});
		ShortRangeScan.updateList(quadrant.klingons, function(klingon) {
			var css="";
			if (klingon.cloaked)
				css = "cloaked ";
			var ratio = klingon.shields/klingon.maxShields;
			if (ratio<0.33)
				css+="damage-bad";
			else if (ratio<0.66)
				css+="damage-medium";
			else if (ratio<1)
				css+="damage-light";
			return {css:css,symbol:klingon.symbol};
		});
		ShortRangeScan.updateList(quadrant.starbases, function(
				starbase) {
			return {css:"",symbol:"&lt;!&gt;"};
		});
		if (Enterprise.quadrant === quadrant)
			ShortRangeScan.updateList([ Enterprise ], function(starhip) {
				return {css:"",symbol:"O=Ξ"};
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
		Tools.defer("ShortRanceScan_updateMap",ShortRangeScan._updateMap);
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
		element.on("click", ShortRangeScan.onSectorClicked);
	},
	onSectorClicked:function(e){
		var cell = $(e.target);
		var id = cell.attr("id");
		var parts = /\w+_(\d)_(\d)/.exec(id);
		if (!parts || !parts.length) //clicked on something else than a cell?
			return;
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
	//function is called often, so we're deferring it and calling only the last one.
	updateQuadrant : function(quadrant) {
		Tools.defer("ShortRangeScanScreen_updateQuadrant",function(){ShortRangeScanScreen._updateQuadrant(quadrant);});
	},
	_updateQuadrant : function(quadrant) {
		var index = 0;
		var qx = quadrant.x;
		var qy = quadrant.y;
		var cells = $("#shortrangescan td");
		for (var y = qy - 1; y <= qy + 1; y++)
			for (var x = qx - 1; x <= qx + 1; x++) {
				var cell = $(cells[index]);
				if (x >= 0 && x <= 7 && y >= 0 && y <= 7) {
					var quadrant = StarMap.getQuadrantAt(x, y);
					quadrant.explored = true;
					LongRangeScanScreen.updateElementWithQuadrant(quadrant,cell);
				} else {
					cell.text("0");
					cell.attr("id", null);
					cell.attr("class","");
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
