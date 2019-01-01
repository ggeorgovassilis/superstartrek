/**
 * Long range scan
 */
var LongRangeScanScreen={
		element:$("#longrangescan"),
		init:function(){
			$("#longrangescan td").on("click",LongRangeScanScreen.onQuadrantSelected);
		},
		onQuadrantSelected:function(e){
			var cell = $(e.currentTarget);
			var id = cell.attr("id");
			window.location.hash="#"+id;
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
			e.text((hasKlingons?"K":" ")+" "+(hasStarbase?"!":" ")+" "+quadrant.stars.length);
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
			e.attr("id","cmd_selectQuadrant_"+quadrant.x+"_"+quadrant.y);
		}
};

LongRangeScanScreen.init();
Events.on(Events.LRS,LongRangeScanScreen.show);
