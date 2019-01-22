package superstartrek.client.model;

import com.google.gwt.user.client.Random;

public class Setup {

	final String names[] = { "Antares", "Siruis", "Rigel", "Deneb", "Procyon", "Capella", "Vega", "Betelgeuse",
			"Canopus", "Aldebaran", "Altair", "Regolus", "Saggitarius","Arcturus","Pollux","Spica" };

	final String roman[]= {"I","II","III","IV"};
	
	protected Quadrant makeQuadrant(int x, int y) {
		Quadrant q = new Quadrant(names[(int)Math.floor((y*8+x)/4)]+ " "+roman[(y*8+x) & 4],x,y);
		int stars = Random.nextInt(Constants.MAX_STARS_IN_QUADRANT);
		while (stars-->0) {
			Star star = new Star();
			star.setName("a star");
			star.setX(Random.nextInt(8));
			star.setY(Random.nextInt(8));
			q.getStars().add(star);
		}
		return q;
	}
	
	public StarMap createNewMap() {
		StarMap map = new StarMap();
		for (int y = 0; y < 8; y++)
			for (int x = 0; x < 8; x++) {
				Quadrant q = makeQuadrant(x, y);
				map.quadrants[x][y] = q;
			}
		return map;
	}
}
