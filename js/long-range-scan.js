/**
 * Long range scan
 */
var LongRangeScanScreen={
		element:$("#longrangescan"),
		show:function(){
			if (!Enterprise.lrsOnline){
				return IO.message(Controller.showComputerScreen, "LRS is offline");
			}
			Tools.updatePageCssWithToken("showLongRangeScan");
			for (var i=0;i<StarMap.quadrants.length;i++)
				LongRangeScanScreen.updateQuadrant(StarMap.quadrants[i]);
//			$("#longrangescan .has-starship")[0].scrollIntoView();
		},
		updateQuadrant:function(quadrant){
			LongRangeScanScreen.updateElementWithQuadrant(quadrant, quadrant.element);
		},
		updateElementWithQuadrant:function(quadrant, e){
			var klingonCount = quadrant.explored?quadrant.klingons.length:0;
			var hasKlingons = klingonCount > 0;
			var hasStarbase = quadrant.starbases.length > 0; 
			e.html((hasKlingons?"K":" ")+" "+(hasStarbase?"!":" ")+" "+quadrant.stars.length);
			e.removeClass("has-starship");
			e.removeClass("has-klingons");
			e.removeClass("has-starbase");
			e.removeClass("explored");
			if (Enterprise.quadrant === quadrant)
				e.addClass("has-starship");
			if (klingonCount>0)
				e.addClass("has-klingons");
			if (quadrant.starbases.length>0)
				e.addClass("has-starbase");
			if (quadrant.explored)
				e.addClass("explored");
			e.attr("id","cmd_selectQuadrant_"+quadrant.x+"_"+quadrant.y);
		}
};
