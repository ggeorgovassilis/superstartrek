/**
 * Long range scan
 */
var LongRangeScanScreen={
		element:$("#longrangescan"),
		init:function(){
			$("#longrangescan").on("click",LongRangeScanScreen.onQuadrantSelected);
		},
		isQuadrantCell:function(cell){
			var x = parseInt(cell.attr("x"));
			return Number.isInteger(x);
		},
		onQuadrantSelected:function(e){
			var cell = $(e.target);
			if (!LongRangeScanScreen.isQuadrantCell(cell))
				return;
			var x = parseInt(cell.attr("x"));
			var y = parseInt(cell.attr("y"));
			Controller.selectQuadrant(x,y);
		},
		show:function(){
			if (!Enterprise.lrsOnline){
				return IO.message("LRS is offline").then.SRS();
			}
			Tools.showScreen("longrangescan");
			StarMap.quadrants.foreach(LongRangeScanScreen.updateQuadrant);
		},
		updateQuadrant:function(quadrant){
			LongRangeScanScreen.updateElementWithQuadrant(quadrant, quadrant.element);
		},
		updateElementWithQuadrant:function(quadrant, e){
			var klingonCount = quadrant.explored?quadrant.klingons.length:0;
			var hasKlingons = klingonCount > 0;
			var hasStarbase = quadrant.starbases.length > 0; 
			Tools.setElementText(e,(hasKlingons?"K":" ")+" "+(hasStarbase?"!":" ")+" "+quadrant.stars.length);
			var css = "";
			if (Enterprise.quadrant === quadrant)
				css="has-starship";
			if (hasKlingons)
				css+=" has-klingons";
			if (hasStarbase)
				css+=" has-starbase";
			if (quadrant.explored)
				css+=" explored";
			e.attr("class",css);
			e.attr("x",quadrant.x);
			e.attr("y",quadrant.y);
		}
};

LongRangeScanScreen.init();
Events.on(Events.LRS,LongRangeScanScreen.show);
