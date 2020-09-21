package superstartrek.client.persistence;

import java.util.HashMap;
import java.util.Map;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import superstartrek.client.Application;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Setting;
import superstartrek.client.model.Star;
import superstartrek.client.model.StarBase;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;
import superstartrek.client.model.Enterprise.ShieldDirection;

public class StarMapDeserialiser {
	
	Application app;
	// many strings (eg. star names) are frequently replicated. Internalising them
	// saves plenty of memory.
	Map<String,String> internedStrings = new HashMap<String, String>();
	
	public StarMapDeserialiser(Application app) {
		this.app = app;
	}
	
	String internalise(String s) {
		if (internedStrings.containsKey(s))
			return internedStrings.get(s);
		internedStrings.put(s,s);
		return s;
	}

	public StarMap readStarMap(String json) {
		StarMap starMap = new StarMap();
		JSONObject jsMap = JSONParser.parseStrict(json).isObject();
		app.starMap = starMap;
		JSONArray jsQuadrants = jsMap.get("quadrants").isArray();
		for (int i = 0; i < jsQuadrants.size(); i++) {
			JSONObject jsQuadrant = (JSONObject) jsQuadrants.get(i);
			Quadrant quadrant = readQuadrant(jsQuadrant, starMap);
			starMap.setQuadrant(quadrant);
		}
		starMap.setStarDate((int)jsMap.get("stardate").isNumber().doubleValue());
		app.gameController.getScoreKeeper().reset();
		app.gameController.getScoreKeeper().addScore((int)jsMap.get("score").isNumber().doubleValue());
		return starMap;
	}

	public Quadrant readQuadrant(JSONObject jsQuadrant, StarMap starMap) {
		String name = internalise(jsQuadrant.get("name").isString().stringValue());
		int x = (int) jsQuadrant.get("x").isNumber().doubleValue();
		int y = (int) jsQuadrant.get("y").isNumber().doubleValue();
		Quadrant q = new Quadrant(name, x, y);
		q.setExplored(jsQuadrant.get("explored").isBoolean().booleanValue());
		JSONArray jsThings = jsQuadrant.get("things").isArray();
		for (int i = 0; i < jsThings.size(); i++) {
			Thing thing = readThing(jsThings.get(i).isObject());
			if (Star.is(thing))
				q.add((Star) thing);
			else if (Klingon.is(thing))
				q.add((Klingon) thing);
			else if (StarBase.is(thing))
				q.setStarBase((StarBase) thing);
			else if (Enterprise.is(thing)) {
				starMap.enterprise = (Enterprise) thing;
				starMap.enterprise.setQuadrant(q);
			}
		}
		return q;
	}

	public Thing readThing(JSONObject jsThing) {
		Thing thing = null;
		String type = internalise(jsThing.get("type").isString().stringValue());
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
		return thing;
	}
	
	public void readSetting(JSONObject jsSetting, Setting setting) {
		setting.setCurrentUpperBound(jsSetting.get("upperBound").isNumber().doubleValue());
		setting.setValue(jsSetting.get("value").isNumber().doubleValue());
		setting.setMaximum(jsSetting.get("max").isNumber().doubleValue());
		setting.setBroken(jsSetting.get("broken").isBoolean().booleanValue());
	}
	
	public void readThing(JSONObject jsThing, Thing thing) {
		thing.setCss(internalise(jsThing.get("css").isString().stringValue()));
		thing.setSymbol(internalise(jsThing.get("symbol").isString().stringValue()));
		thing.setLocation(Location.location((int)jsThing.get("x").isNumber().doubleValue(), (int)jsThing.get("y").isNumber().doubleValue()));
		thing.setName(internalise(jsThing.get("name").isString().stringValue()));
	}
	
	public void readVessel(JSONObject jsThing, Vessel vessel) {
		readThing(jsThing, vessel);
		readSetting(jsThing.get("impulse").isObject(), vessel.getImpulse());
		readSetting(jsThing.get("shields").isObject(), vessel.getShields());
	}
	
	public Star readStar(JSONObject jsThing) {
		Star star = new Star();
		readThing(jsThing, star);
		return star;
	}

	public StarBase readStarBase(JSONObject jsThing) {
		StarBase sb = new StarBase();
		readThing(jsThing, sb);
		return sb;
	}

	public Klingon readKlingon(JSONObject jsThing) {
		Klingon k = new Klingon();
		readVessel(jsThing, k);
		readSetting(jsThing.get("disruptor").isObject(), k.getDisruptor());
		readSetting(jsThing.get("cloak").isObject(), k.getCloak());
		return k;
	}

	public Enterprise readEnterprise(JSONObject jsThing) {
		Enterprise e = new Enterprise(app, app.starMap);
		readVessel(jsThing, e);
		readSetting(jsThing.get("antimatter").isObject(), e.getAntimatter());
		readSetting(jsThing.get("autoaim").isObject(), e.getAutoAim());
		readSetting(jsThing.get("lrs").isObject(), e.getLrs());
		readSetting(jsThing.get("phasers").isObject(), e.getPhasers());
		readSetting(jsThing.get("reactor").isObject(), e.getReactor());
		readSetting(jsThing.get("torpedos").isObject(), e.getTorpedos());
		e.setShieldDirection(ShieldDirection.valueOf(jsThing.get("shieldsDirection").isString().stringValue()));
		return e;
	}
}
