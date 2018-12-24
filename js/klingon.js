/**
 * Klingon AI
 */

var Klingons={
		play:function(klingon, quadrant){
			if (StarShip.quadrant != quadrant)
				return;
			// can raider fire on us?
			var obstacle = Tools.findObstruction(quadrant, klingon.x, klingon.y, StarShip.x, StarShip.y);
			if (obstacle)
				Klingons.manueverIntoFiringPosition(klingon, StarShip.quadrant);
			else
				Klingons.fireOnStarship(klingon);
		},
		manueverIntoFiringPosition:function(klingon, quadrant){
			//find a spot which:
			//1. is empty
			//2. raider has a clear shot at us 
			//3. raider can move to unobstructed 
			//4. isn't more than 2 squares from original
			for (var x=0;x<8;x++)
			for (var y=0;y<8;y++){
				if (Tools.distance(x,y,klingon.x,klingon.y)>2)
					continue;
				var thing = StarMap.getAnythingInQuadrantAt(quadrant, x, y);
				if (thing)
					continue;
				var obstacle = Tools.findObstruction(quadrant, x, y, StarShip.x, StarShip.y);
				if (obstacle)
					continue;
				obstacle = Tools.findObstruction(quadrant, klingon.x, klingon.y, x, y);
				if (obstacle)
					continue;
				klingon.x = x;
				klingon.y = y;
				return;
			}
		},
		fireOnStarship:function(klingon){
			StarShip.shields = StarShip.shields - klingon.weaponPower;
			StarShip.maxShields = StarShip.maxShields*4/5;
			StarShip.shields = Math.min(StarShip.shields,StarShip.maxShields);
			if (StarShip.shields < 0){
				return IO.gameOverMessage("Klingon ship destroyed us, game over.");
			}
			return IO.message(function(){}, "Klingon ship fired at us, shields dropped to "+Math.round(StarShip.shields));
		},
		damage:function(klingon, damage){
			klingon.shields-=damage;
			if (klingon.shields>0){
				$window.trigger("klingon_damaged",[klingon]);
			} else {
				Klingons.destroy(klingon);
			}
		},
		destroy:function(klingon){
			klingon.quadrant.klingons.remove(klingon);
			$window.trigger("klingon_destroyed",[klingon])
		},
		on_klingon_damaged:function(){
			IO.message(null,"Target hit");
		},
		on_klingon_destroyed:function(event,klingon){
			IO.message(null,"Target destroyed");
		}
};

$(window).on("klingon_damaged",Klingons.on_klingon_damaged);
$(window).on("klingon_destroyed",Klingons.on_klingon_destroyed);

