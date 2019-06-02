package superstartrek.client.model;

import superstartrek.client.Application;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.model.Star.StarClass;
import superstartrek.client.utils.BrowserAPI;

public class Setup {

	final String names[] = { "Antares", "Siruis", "Rigel", "Deneb", "Procyon", "Capella", "Vega", "Betelgeuse",
			"Canopus", "Aldebaran", "Altair", "Regolus", "Saggitarius","Arcturus","Pollux","Spica" };

	final String roman[]= {"I","II","III","IV"};
	
	final Application application;
	
	public Setup(Application application) {
		this.application = application;
	}
	
	void maybeShouldWarn(int currentSectors, int hardcodedSectors) {
		if (currentSectors!=hardcodedSectors)
			throw new RuntimeException("Names have not beed coded for anything but 8x8 quadrants yet");
	}
	
	public Quadrant makeQuadrant(StarMap map, int x, int y) {
		maybeShouldWarn(Constants.SECTORS_EDGE, 8);
		Quadrant q = new Quadrant(names[(int)Math.floor((y*8+x)/4)]+ " "+roman[(y*8+x) % 4],x,y);
		BrowserAPI random = Application.get().browserAPI;
		int stars = Constants.MIN_STARS_IN_QUADRANT+random.nextInt(Constants.MAX_STARS_IN_QUADRANT-Constants.MIN_STARS_IN_QUADRANT);
		while (stars-->0) {
			//TODO: this is potentially slow, as findFreeSpot creates a QuadrantIndex over and over
			Location loc = map.findFreeSpot(q);
			Star star = new Star(loc.getX(), loc.getY(), StarClass.values()[random.nextInt(StarClass.values().length)]);
			q.getStars().add(star);
		}
		if ((x+y*Constants.SECTORS_EDGE) % ((Constants.SECTORS_EDGE*Constants.SECTORS_EDGE)/Constants.NO_OF_STARBASES_ON_MAP) ==0) {
			StarBase starBase = new StarBase();
			Location loc = map.findFreeSpot(q);
			starBase.setLocation(loc);
			q.setExplored(true);
			q.setStarBase(starBase);
		}
		//don't put klingons in start quadrant
		if (x+y!=0 && random.nextDouble()<Constants.CHANCE_OF_KLINGONS_IN_QUADRANT) {
			int klingons = 1+random.nextInt(Constants.MAX_KLINGONS_IN_QUADRANT);
			while (klingons-->0) {
				int cIndex = random.nextInt(Klingon.ShipClass.values().length);
				Klingon k = new Klingon((Klingon.ShipClass.values()[cIndex]));
				Location loc = map.findFreeSpot(q);
				k.setLocation(loc);
				q.getKlingons().add(k);
			}
		}
		return q;
	}
	
	public StarMap createNewMap() {
		StarMap map = new StarMap();
		map.enterprise = new Enterprise(application, map);
		for (int y = 0; y < Constants.SECTORS_EDGE; y++)
			for (int x = 0; x < Constants.SECTORS_EDGE; x++) {
				Quadrant q = makeQuadrant(map, x, y);
				map.setQuadrant(q);;
			}
		
		//TODO: find free spot
		map.enterprise.setQuadrant(map.quadrants[0][0]);
		map.enterprise.getQuadrant().setExplored(true);
		map.enterprise.setLocation(map.findFreeSpot(map.quadrants[0][0]));
		return map;
	}
}
