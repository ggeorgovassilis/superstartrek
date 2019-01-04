/**
 * Constants.
 * Refer to wikipedia articles on stardate and warpspeed for why constants were assigned their current values.
 * Phasers lose power over distance in the original game. No information about disruptors.
 */

var nop = function(){};

var Constants = {
		DURATION_OF_MOVEMENT_PER_SECTOR: 0.05,
		DURATION_OF_MOVEMENT_PER_QUADRANT: 1,
		DURATION_OF_ROUND:1,
		DURATION_OF_REFUELING:2,
		DURATION_OF_REPAIRS:4,
		DURATION_OF_PROVISIONAL_REPAIRS:8,
		ENERGY_OF_MOVEMENT_PER_SECTOR: 20,
		ENERGY_PER_SHIELD:2,
		BASE_CONSUMPTION:10,
		ENERGY_OF_MOVEMENT_PER_QUADRANT_PER_WARP: function(speed){
			return 10+speed*speed*speed*2;
		},
		ENTERPRISE_MAX_SHIELDS:100,
		ENTERPRISE_MAX_WARP_SPEED:8,
		MAX_ENERGY:4000,
		MAX_TORPEDOS:10,
		MAX_TORPEDO_DAMAGE:50,
		MAX_IMPULSE_SPEED:3,
		MAX_WARP_SPEED:4,
		MAX_REACTOR_OUTPUT:290,
		PHASER_EFFICIENCY:1,
		ENTERPRISE_MAX_PHASER_POWER:45,
		KLINGON_DISRUPTOR_POWER:20,
		KLINGON_IMPULSE_SPEED:1,//must be integer because of use in a*
		DISRUPTOR_RANGE:3,
		PHASER_RANGE:3,
		SMALL_HEIGHT:450,
		SMALL_WIDTH:621,
		CHANCE_OF_STARBASE_IN_QUADRANT:0.09,
		CHANCE_OF_KLINGON_IN_QUADRANT:0.3,
		MAX_KLINGON_SHIELD:80,
};

/*
 * Starmap
 */

var x=0;
var y=0;

var StarMap={
		constructQuadrants:function(){
		StarMap.quadrants =
		[
		Setup.makeQuadrant("Antares I"  ,x=0,y=0),
		Setup.makeQuadrant("Antares II" ,++x,y),
		Setup.makeQuadrant("Antares III",++x,y),
		Setup.makeQuadrant("Antares IV" ,++x,y),
		Setup.makeQuadrant("Sirius I"   ,++x,y),
		Setup.makeQuadrant("Sirius II"  ,++x,y),
		Setup.makeQuadrant("Sirius III" ,++x,y),
		Setup.makeQuadrant("Sirius IV"  ,++x,y),
		Setup.makeQuadrant("Rigel I"    ,x=0,++y),
		Setup.makeQuadrant("Rigel II"   ,++x,y),
		Setup.makeQuadrant("Rigel III"  ,++x,y),
		Setup.makeQuadrant("Rigel IV"   ,++x,y),
		Setup.makeQuadrant("Deneb I"    ,++x,y),
		Setup.makeQuadrant("Deneb II"   ,++x,y),
		Setup.makeQuadrant("Deneb  III" ,++x,y),
		Setup.makeQuadrant("Deneb  IV"  ,++x,y),
		Setup.makeQuadrant("Procyon I"    ,x=0,++y),
		Setup.makeQuadrant("Procyon II"   ,++x,y),
		Setup.makeQuadrant("Procyon III"  ,++x,y),
		Setup.makeQuadrant("Procyon IV"   ,++x,y),
		Setup.makeQuadrant("Capella I"    ,++x,y),
		Setup.makeQuadrant("Capella II"   ,++x,y),
		Setup.makeQuadrant("Capella III" ,++x,y),
		Setup.makeQuadrant("Capella IV"  ,++x,y),
		Setup.makeQuadrant("Vega I"    ,x=0,++y),
		Setup.makeQuadrant("Vega II"   ,++x,y),
		Setup.makeQuadrant("Vega III"  ,++x,y),
		Setup.makeQuadrant("Vega IV"   ,++x,y),
		Setup.makeQuadrant("Betelgeuse I"    ,++x,y),
		Setup.makeQuadrant("Betelgeuse II"   ,++x,y),
		Setup.makeQuadrant("Betelgeuse III" ,++x,y),
		Setup.makeQuadrant("Betelgeuse IV"  ,++x,y),
		Setup.makeQuadrant("Canopus I"    ,x=0,++y),
		Setup.makeQuadrant("Canopus II"   ,++x,y),
		Setup.makeQuadrant("Canopus III"  ,++x,y),
		Setup.makeQuadrant("Canopus IV"   ,++x,y),
		Setup.makeQuadrant("Aldebaran I"    ,++x,y),
		Setup.makeQuadrant("Aldebaran II"   ,++x,y),
		Setup.makeQuadrant("Aldebaran III" ,++x,y),
		Setup.makeQuadrant("Aldebaran IV"  ,++x,y),
		Setup.makeQuadrant("Altair I"    ,x=0,++y),
		Setup.makeQuadrant("Altair II"   ,++x,y),
		Setup.makeQuadrant("Altair III"  ,++x,y),
		Setup.makeQuadrant("Altair IV"   ,++x,y),
		Setup.makeQuadrant("Regulus I"    ,++x,y),
		Setup.makeQuadrant("Regulus II"   ,++x,y),
		Setup.makeQuadrant("Regulus III" ,++x,y),
		Setup.makeQuadrant("Regulus IV"  ,++x,y),
		Setup.makeQuadrant("Sagittarius I"    ,x=0,++y),
		Setup.makeQuadrant("Sagittarius II"   ,++x,y),
		Setup.makeQuadrant("Sagittarius III"  ,++x,y),
		Setup.makeQuadrant("Sagittarius IV"   ,++x,y),
		Setup.makeQuadrant("Arcturus I"    ,++x,y),
		Setup.makeQuadrant("Arcturus II"   ,++x,y),
		Setup.makeQuadrant("Arcturus III" ,++x,y),
		Setup.makeQuadrant("Arcturus IV"  ,++x,y),
		Setup.makeQuadrant("Pollux I"    ,x=0,++y),
		Setup.makeQuadrant("Pollux II"   ,++x,y),
		Setup.makeQuadrant("Pollux III"  ,++x,y),
		Setup.makeQuadrant("Pollux IV"   ,++x,y),
		Setup.makeQuadrant("Spica I"    ,++x,y),
		Setup.makeQuadrant("Spica II"   ,++x,y),
		Setup.makeQuadrant("Spica III" ,++x,y),
		Setup.makeQuadrant("Spica IV"  ,++x,y)
		];
			},
		   getQuadrantAt:function(x,y){
			   for (var i=0;i<StarMap.quadrants.length;i++){
				   var quadrant = StarMap.quadrants[i];
				   if (quadrant.x === x && quadrant.y === y)
					   return quadrant;
			   }
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




var CommandBar={
		element:$("#commandbar")
};

Events.on(Events.SETTINGS_CHANGED, Controller.updateFireAtWillButton);
Events.on(Events.ENTERPRISE_DAMAGED, Computer.updateShieldsIndicator);
Events.on(Events.ENTERPRISE_DAMAGED, Computer.updateDamagedIndicator);
Events.on(Events.ENTERPRISE_REPAIRED, Computer.updateDamagedIndicator);
//window.onbeforeunload = function(e){
//		return ""; 
//};

