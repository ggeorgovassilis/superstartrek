package superstartrek.client.model;

import java.util.ArrayList;
import java.util.List;

import superstartrek.client.Application;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.model.Star.StarClass;
import superstartrek.client.utils.BrowserAPI;

public class Setup {

	public static enum Formations {
		f1(1, 0), f2(0, 1), f3(5, 0), f4(3, 1), f5(0, 2), f6(0, 3), f7(3, 3);

		Formations(int raiders, int birdsOfPrey) {
			this.raiders = raiders;
			this.birdsOfPrey = birdsOfPrey;
		}

		final int raiders;
		final int birdsOfPrey;
	}

	final List<Formations> formations = new ArrayList<>();

	final String names[] = { "Antares", "Siruis", "Rigel", "Deneb", "Procyon", "Capella", "Vega", "Betelgeuse",
			"Canopus", "Aldebaran", "Altair", "Regolus", "Saggitarius", "Arcturus", "Pollux", "Spica" };

	final String roman[] = { "I", "II", "III", "IV" };

	final Application application;

	public Setup(Application application) {
		this.application = application;
		formations.add(Formations.f1);
		formations.add(Formations.f1);
		formations.add(Formations.f1);
		formations.add(Formations.f1);
		formations.add(Formations.f2);
		formations.add(Formations.f2);
		formations.add(Formations.f3);
		formations.add(Formations.f3);
		formations.add(Formations.f4);
		formations.add(Formations.f4);
		formations.add(Formations.f5);
		formations.add(Formations.f5);
		formations.add(Formations.f6);
		formations.add(Formations.f6);
		formations.add(Formations.f7);
	}

	void maybeShouldWarn(int currentSectors, int hardcodedSectors) {
		if (currentSectors != hardcodedSectors)
			throw new RuntimeException("Names have not beed coded for anything but 8x8 quadrants yet");
	}

	void deployFormationIntoQuadrant(Quadrant quadrant, BrowserAPI random, StarMap map, Formations f) {
		for (int i = 0; i < f.raiders; i++) {
			Klingon k = new Klingon(Klingon.ShipClass.Raider);
			Location loc = map.findFreeSpot(quadrant);
			k.setLocation(loc);
			quadrant.add(k);
		}
		for (int i = 0; i < f.birdsOfPrey; i++) {
			Klingon k = new Klingon(Klingon.ShipClass.BirdOfPrey);
			Location loc = map.findFreeSpot(quadrant);
			k.setLocation(loc);
			quadrant.add(k);
		}
	}

	public Quadrant makeQuadrant(StarMap map, int x, int y) {
		maybeShouldWarn(Constants.SECTORS_EDGE, 8);
		Quadrant q = new Quadrant(names[(int) Math.floor((y * 8 + x) / 4)] + " " + roman[(y * 8 + x) % 4], x, y);
		BrowserAPI random = Application.get().browserAPI;
		int stars = Constants.MIN_STARS_IN_QUADRANT
				+ random.nextInt(Constants.MAX_STARS_IN_QUADRANT - Constants.MIN_STARS_IN_QUADRANT);
		while (stars-- > 0) {
			// TODO: this is potentially slow, as findFreeSpot creates a QuadrantIndex over
			// and over. on the other hand, quadrants are made only once at the beginning of the game
			Location loc = map.findFreeSpot(q);
			Star star = new Star(loc.getX(), loc.getY(), StarClass.values()[random.nextInt(StarClass.values().length)]);
			q.add(star);
		}
		if ((x + y * Constants.SECTORS_EDGE)
				% ((Constants.SECTORS_EDGE * Constants.SECTORS_EDGE) / Constants.NO_OF_STARBASES_ON_MAP) == 0) {
			StarBase starBase = new StarBase();
			Location loc = map.findFreeSpot(q);
			starBase.setLocation(loc);
			q.setExplored(true);
			q.setStarBase(starBase);
		}
		return q;
	}

	public void putKlingonsInQuadrant(Quadrant q, StarMap map) {
		if (q.x + q.y != 0 && !formations.isEmpty()) {
			BrowserAPI random = Application.get().browserAPI;
			int index = random.nextInt(formations.size());
			Formations f = formations.remove(index);
			deployFormationIntoQuadrant(q, random, map, f);
		}
	}

	public void putKlingonsOnStarMap(StarMap map) {
		BrowserAPI random = Application.get().browserAPI;
		while (!formations.isEmpty()) {
			int x = random.nextInt(Constants.SECTORS_EDGE);
			int y = random.nextInt(Constants.SECTORS_EDGE);
			Quadrant q = map.getQuadrant(x, y);
			putKlingonsInQuadrant(q, map);
		}
	}

	public StarMap createNewMap() {
		StarMap map = new StarMap();
		Enterprise enterprise = new Enterprise(application, map);
		map.enterprise = enterprise;
		for (int y = 0; y < Constants.SECTORS_EDGE; y++)
			for (int x = 0; x < Constants.SECTORS_EDGE; x++) {
				Quadrant q = makeQuadrant(map, x, y);
				map.setQuadrant(q);
			}

		// TODO: find free spot
		enterprise.setQuadrant(map.quadrants[0][0]);
		enterprise.getQuadrant().setExplored(true);
		enterprise.setLocation(map.findFreeSpot(map.quadrants[0][0]));
		enterprise.getQuadrant().addEnterprise(enterprise);
		putKlingonsOnStarMap(map);
		return map;
	}
}
