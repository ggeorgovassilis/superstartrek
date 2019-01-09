/*
 * Starmap
 */
var StarMap={
		   constructQuadrants:function(){
			   Setup.makeStarMap(StarMap);
		   },
		   getQuadrantAt:function(x,y){
			   var quadrant = StarMap.quadrants[x+y*8];
			   if (x!=quadrant.x || y!=quadrant.y)
				   throw "Mislocated quadrant. Requested "+x+":"+y+" but got "+quadrant.x+":"+quadrant.y;
			   return quadrant;
		   },
		   getThingFromListAt:function(list,x,y){
			   for (var i=0;i<list.length;i++){
				   var thing = list[i];
				   if (thing.x === x && thing.y === y)
					   return thing;
			   }
		   },
		   getKlingonInQuadrantAt:function(quadrant, x, y){
			   return StarMap.getThingFromListAt(quadrant.klingons, x,y);
		   },
		   getAnythingInQuadrantAt:function(quadrant, x, y){
			   var thing = StarMap.getKlingonInQuadrantAt(quadrant, x, y);
			   if (thing)
				   return thing;
			   thing = StarMap.getThingFromListAt(quadrant.starbases, x,y);
			   if (thing)
				   return thing;
			   thing = StarMap.getThingFromListAt(quadrant.stars, x,y);
			   if (thing)
				   return thing;
			   if (Enterprise.quadrant === quadrant && Enterprise.x === x && Enterprise.y === y)
				   return Enterprise;
		   },
		   isStarbaseAdjacent:function(quadrant, x, y){
			 for (var i=0;i<quadrant.starbases.length;i++){
				 var starbase = quadrant.starbases[i];
				 for (var _x=x-1;_x<=x+1;_x++)  
			     for (var _y=y-1;_y<=y+1;_y++)
			     if (starbase.x===_x && starbase.y===_y)
			    	 return starbase;
			 }
			 return false;
		   },
		   countKlingons:function(){
			   var count = 0;
			   for (var i=0;i<StarMap.quadrants.length;i++)
				   count+=StarMap.quadrants[i].klingons.length;
			   return count;
		   }
};
