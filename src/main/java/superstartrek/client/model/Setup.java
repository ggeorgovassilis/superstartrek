package superstartrek.client.model;

import com.google.gwt.user.client.Random;

import superstartrek.client.Application;
import superstartrek.client.activities.klingons.Klingon;

public class Setup {

	final String names[] = { "Antares", "Siruis", "Rigel", "Deneb", "Procyon", "Capella", "Vega", "Betelgeuse",
			"Canopus", "Aldebaran", "Altair", "Regolus", "Saggitarius","Arcturus","Pollux","Spica" };

	final String roman[]= {"I","II","III","IV"};
	
	final Application application;
	
	public Setup(Application application) {
		this.application = application;
	}
	
	protected Quadrant makeQuadrant(StarMap map, int x, int y) {
		Quadrant q = new Quadrant(names[(int)Math.floor((y*8+x)/4)]+ " "+roman[(y*8+x) & 4],x,y);
		int stars = Random.nextInt(Constants.MAX_STARS_IN_QUADRANT);
		while (stars-->0) {
			Star star = new Star(application);
			Location loc = map.findFreeSpot(q);
			star.setLocation(loc);
			star.setQuadrant(q);
			q.getStars().add(star);
		}
		if ((x+y*8) % ((int)(64/Constants.NO_OF_STARBASES_ON_MAP)) ==0) {
			StarBase starBase = new StarBase();
			Location loc = map.findFreeSpot(q);
			starBase.setLocation(loc);
			starBase.setQuadrant(q);
			q.setExplored(true);
			q.setStarBase(starBase);
		}
		if (Random.nextDouble()<Constants.CHANCE_OF_KLINGONS_IN_QUADRANT) {
			int klingons = 1+Random.nextInt(Constants.MAX_KLINGONS_IN_QUADRANT-1);
			while (klingons-->0) {
				int cIndex = Random.nextInt(Klingon.ShipClass.values().length);
				Klingon k = new Klingon(application, Klingon.ShipClass.values()[cIndex]);
				k.setQuadrant(q);
				Location loc = map.findFreeSpot(q);
				k.setLocation(loc);
				q.getKlingons().add(k);
			}
		}
		return q;
	}
	
	public StarMap createNewMap() {
		StarMap map = new StarMap();
		map.enterprise = new Enterprise(application);
		for (int y = 0; y < 8; y++)
			for (int x = 0; x < 8; x++) {
				Quadrant q = makeQuadrant(map, x, y);
				map.quadrants[x][y] = q;
			}
		
		//TODO: find free spot
		map.enterprise.setQuadrant(map.quadrants[0][0]);
		map.enterprise.getQuadrant().setExplored(true);
		map.enterprise.setLocation(map.findFreeSpot(map.quadrants[0][0]));
		return map;
	}
}