package superstartrek.client.persistence;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;

import superstartrek.client.Application;
import superstartrek.client.activities.klingons.Klingon;
import superstartrek.client.model.Constants;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Setting;
import superstartrek.client.model.Star;
import superstartrek.client.model.StarBase;
import superstartrek.client.model.StarMap;
import superstartrek.client.model.Thing;
import superstartrek.client.model.Vessel;

public class StarMapSerialiser {

	StringBuffer sb = new StringBuffer();
	Application app;
	
	public StarMapSerialiser(Application app) {
		this.app = app;
	}
	
	public void serialise(Thing thing) {
		sb.append("\t{\"name\":\""+thing.getName()+"\",\n");
		sb.append("\t\"x\":"+thing.getLocation().getX()+",\n");
		sb.append("\t\"y\":"+thing.getLocation().getY()+",\n");
		sb.append("\t\"css\":\""+thing.getCss()+"\",\n");
		sb.append("\t\"symbol\":\""+thing.getSymbol()+"\",\n");
		if (Star.is(thing))
			subserialise((Star)thing);
		if (Vessel.is(thing))
			subserialise((Vessel)thing);
		if (Klingon.is(thing))
			subserialise((Klingon)thing);
		if (Enterprise.is(thing))
			subserialise((Enterprise)thing);
		if (thing instanceof StarBase)
			subserialise((StarBase)thing);
		sb.append("\"_\":\"\"");
		sb.append("\t}");
	}
	
	public void subserialise(Enterprise enterprise) {
		sb.append("\t\"type\":\"enterprise\",\n");
		sb.append("\t\"phasers\":");
		subserialise(enterprise.getPhasers());
		sb.append(",\n");
		sb.append("\t\"torpedos\":");
		subserialise(enterprise.getTorpedos());
		sb.append(",\n");
		sb.append("\t\"antimatter\":");
		subserialise(enterprise.getAntimatter());
		sb.append(",\n");
		sb.append("\t\"autoaim\":");
		subserialise(enterprise.getAutoAim());
		sb.append(",\n");
		sb.append("\t\"autorepair\":");
		subserialise(enterprise.getAutoRepair());
		sb.append(",\n");
		sb.append("\t\"lrs\":");
		subserialise(enterprise.getLrs());
		sb.append(",\n");
		sb.append("\t\"reactor\":");
		subserialise(enterprise.getReactor());
		sb.append(",\n");
	}
	
	public void subserialise(Setting setting) {
		sb.append("{\"value\":"+setting.getValue()+",\"max\":"+setting.getMaximum()+",\"upperBound\":"+setting.getCurrentUpperBound()+"}");
	}

	public void subserialise(Vessel vessel) {
		sb.append("\t\"impulse\":");
		subserialise(vessel.getImpulse());
		sb.append(",\n");
		sb.append("\t\"shields\":");
		subserialise(vessel.getShields());
		sb.append(",\n");
	}

	public void subserialise(Klingon klingon) {
		sb.append("\t\"type\":\"klingon\",\n");
		sb.append("\t\"disruptor\":");
		subserialise(klingon.getDisruptor());
		sb.append(",\n");
		sb.append("\t\"cloak\":");
		subserialise(klingon.getCloak());
		sb.append(",\n");
		sb.append("\"xp\":"+klingon.getXp()+",\n");
	}

	public void subserialise(Star star) {
		sb.append("\t\"type\":\"star\",\n");
	}

	public void subserialise(StarBase starbase) {
		sb.append("\t\"type\":\"starbase\",\n");
	}

	public void serialise(StarMap map, Quadrant quadrant) {
		sb.append("{");
		sb.append("\"name\":\"").append(quadrant.getName()).append("\",\n");
		sb.append("\"x\":").append(quadrant.getX()).append(",\n");
		sb.append("\"y\":").append(quadrant.getY()).append(",\n");
		sb.append("\"explored\":").append(quadrant.isExplored()).append(",\n");
		sb.append("\"things\":[");
		List<Thing> things = map.getEverythingIn(quadrant);
		for (Thing thing:things) {
			serialise(thing);
			if (things.get(things.size()-1) != thing)
				sb.append("\n,");
		}
		sb.append("]");
		sb.append("}");
	}
	
	public String serialise(StarMap map) {
		sb.append("{\n\"quadrants\":");
		sb.append("[");
		for (int y=0;y<Constants.SECTORS_EDGE;y++) 
		for (int x=0;x<Constants.SECTORS_EDGE;x++) {
				serialise(map, map.getQuadrant(x, y));
				if (!(x==Constants.SECTORS_EDGE-1 && x==y))
					sb.append(",\n");
			}
		sb.append("],\n");
		sb.append("\"stardate\":"+map.getStarDate()).append(",\n");
		sb.append("\"score\":"+app.gameController.getScoreKeeper().getScore()).append("\n");
		sb.append("}");
		return sb.toString();
	}
	
}
