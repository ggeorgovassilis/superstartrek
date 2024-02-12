package superstartrek.client.persistence;

import java.util.HashMap;
import java.util.Map;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import superstartrek.client.Application;
import superstartrek.client.control.ScoreKeeper;
import superstartrek.client.space.Location;
import superstartrek.client.space.Quadrant;
import superstartrek.client.space.Setting;
import superstartrek.client.space.Star;
import superstartrek.client.space.StarBase;
import superstartrek.client.space.StarMap;
import superstartrek.client.space.Thing;
import superstartrek.client.space.Star.StarClass;
import superstartrek.client.vessels.Enterprise;
import superstartrek.client.vessels.Klingon;
import superstartrek.client.vessels.Vessel;
import superstartrek.client.vessels.Enterprise.ShieldDirection;

/**
 * Reads {@link StarMap} instances from a string. Single use & throw away, don't hold
 * on to {@link StarMapDeserialiser} instances for multiple runs.
 */
class StarMapDeserialiser {

	Application app;
	// many strings (eg. star names) are frequently replicated. Internalising them
	// saves plenty of memory.
	Map<String, String> internedStrings = new HashMap<String, String>();

	public StarMapDeserialiser(Application app) {
		this.app = app;
	}

	//Profiling showed considerable memory taken up by string duplicates
	//GWT 2.9 String.intern() doesn't seem to do anything, so we're going with a custom implementation.
	String internalise(String s) {
		if (internedStrings.containsKey(s))
			return internedStrings.get(s);
		internedStrings.put(s, s);
		return s;
	}
	
	double _double(JSONObject o, String attribute) {
		return o.get(attribute).isNumber().doubleValue();
	}

	boolean _boolean(JSONObject o, String attribute) {
		return o.get(attribute).isBoolean().booleanValue();
	}

	String _string(JSONObject o, String attribute) {
		return o.get(attribute).isString().stringValue();
	}

	public StarMap readStarMap(String json) {
		StarMap starMap = app.starMap = new StarMap();
		JSONObject jsMap = JSONParser.parseStrict(json).isObject();
		JSONArray jsQuadrants = jsMap.get("quadrants").isArray();
		for (int i = 0; i < jsQuadrants.size(); i++) {
			JSONObject jsQuadrant = jsQuadrants.get(i).isObject();
			Quadrant quadrant = readQuadrant(jsQuadrant, starMap);
			starMap.setQuadrant(quadrant);
		}
		starMap.setStarDate((int) _double(jsMap,"stardate"));
		ScoreKeeper scoreKeeper = app.gameController.getScoreKeeper();
		scoreKeeper.reset();
		scoreKeeper.addScore((int) _double(jsMap,"score"));
		return starMap;
	}

	public Quadrant readQuadrant(JSONObject jsQuadrant, StarMap starMap) {
		String name = internalise(_string(jsQuadrant,"name"));
		int x = (int) _double(jsQuadrant,"x");
		int y = (int) _double(jsQuadrant,"y");
		Quadrant q = new Quadrant(name, x, y);
		q.setExplored(_boolean(jsQuadrant, "explored"));
		JSONArray jsThings = jsQuadrant.get("things").isArray();
		for (int i = 0; i < jsThings.size(); i++) {
			Thing thing = readThing(jsThings.get(i));
			if (Star.is(thing))
				q.add((Star) thing);
			else if (Klingon.is(thing))
				q.add((Klingon) thing);
			else if (StarBase.is(thing))
				q.setStarBase(thing.as());
			else if (Enterprise.is(thing)) {
				starMap.enterprise = thing.as();
				starMap.enterprise.setQuadrant(q);
				q.add(starMap.enterprise);
			}
		}
		q.dehydrate();
		return q;
	}

	public Thing readThing(JSONValue jsValue) {
		JSONObject jsThing = jsValue.isObject();
		Thing thing = null;
		String type = _string(jsThing, "type");
		switch (type) {
		case "star":
			thing = readStar(jsThing);
			break;
		case "starbase":
			thing = readStarBase(jsThing);
			break;
		case "klingon":
			thing = readKlingon(jsThing);
			break;
		case "enterprise":
			thing = readEnterprise(jsThing);
			break;
		}
		Location location = readLocation(jsThing);
		thing.setLocation(location);
		return thing;
	}

	public void readSetting(JSONValue jsValue, Setting setting) {
		JSONObject jsSetting = jsValue.isObject();
		setting.setCurrentUpperBound(_double(jsSetting, "upperBound"));
		setting.setValue(_double(jsSetting,"value"));
		setting.setMaximum(_double(jsSetting,"max"));
		setting.setBroken(_boolean(jsSetting, "broken"));
	}

	public void readVessel(JSONObject jsThing, Vessel vessel) {
		readSetting(jsThing.get("impulse"), vessel.getImpulse());
		readSetting(jsThing.get("shields"), vessel.getShields());
	}

	Location readLocation(JSONObject jsThing) {
		int x = (int) _double(jsThing, "x");
		int y = (int) _double(jsThing, "y");
		return Location.location(x, y);
	}

	public Star readStar(JSONObject jsThing) {
		Location location = readLocation(jsThing);
		StarClass sc = StarClass.valueOf(_string(jsThing, "starclass"));
		Star star = new Star(location, sc);
		return star;
	}

	public StarBase readStarBase(JSONObject jsThing) {
		StarBase sb = new StarBase();
		return sb;
	}

	public Klingon readKlingon(JSONObject jsThing) {
		Klingon k = new Klingon();
		readVessel(jsThing, k);
		readSetting(jsThing.get("disruptor"), k.getDisruptor());
		readSetting(jsThing.get("cloak"), k.getCloak());
		return k;
	}

	public Enterprise readEnterprise(JSONObject jsThing) {
		Enterprise e = new Enterprise(app, app.starMap);
		readVessel(jsThing, e);
		readSetting(jsThing.get("antimatter"), e.getAntimatter());
		readSetting(jsThing.get("autoaim"), e.getAutoAim());
		readSetting(jsThing.get("lrs"), e.getLrs());
		readSetting(jsThing.get("phasers"), e.getPhasers());
		readSetting(jsThing.get("reactor"), e.getReactor());
		readSetting(jsThing.get("torpedos"), e.getTorpedos());
		e.setShieldDirection(ShieldDirection.valueOf(_string(jsThing, "shieldsDirection")));
		return e;
	}
}
