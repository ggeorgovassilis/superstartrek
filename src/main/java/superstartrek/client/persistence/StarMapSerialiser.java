package superstartrek.client.persistence;

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
	
	void closeScope(String suffix) {
		if (sb.charAt(sb.length()-1) == ',')
			sb.deleteCharAt(sb.length()-1);
		sb.append(suffix);
	}
	
	public StarMapSerialiser(Application app) {
		this.app = app;
	}
	
	public void serialise(Thing thing) {
		sb.append("{\"name\":\""+thing.getName()+"\",");
		sb.append("\"x\":"+thing.getLocation().x+",");
		sb.append("\"y\":"+thing.getLocation().y+",");
		sb.append("\"css\":\""+thing.getCss()+"\",");
		sb.append("\"symbol\":\""+thing.getSymbol()+"\",");
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
		closeScope("}");
	}
	
	public void subserialise(Enterprise enterprise) {
		sb.append("\"type\":\"enterprise\",");
		sb.append("\"phasers\":");
		subserialise(enterprise.getPhasers());
		sb.append(",");
		sb.append("\"torpedos\":");
		subserialise(enterprise.getTorpedos());
		sb.append(",");
		sb.append("\"antimatter\":");
		subserialise(enterprise.getAntimatter());
		sb.append(",");
		sb.append("\"autoaim\":");
		subserialise(enterprise.getAutoAim());
		sb.append(",");
		sb.append("\"lrs\":");
		subserialise(enterprise.getLrs());
		sb.append(",");
		sb.append("\"reactor\":");
		subserialise(enterprise.getReactor());
		sb.append(",");
		sb.append("\"shieldsDirection\":\"").append(enterprise.getShieldDirection().toString()).append("\",");
	}
	
	public void subserialise(Setting setting) {
		sb.append("{\"value\":"+setting.getValue()+",\"max\":"+setting.getMaximum()+",\"upperBound\":"+setting.getCurrentUpperBound()+", \"broken\":"+setting.isBroken()+"}");
	}

	public void subserialise(Vessel vessel) {
		sb.append("\"impulse\":");
		subserialise(vessel.getImpulse());
		sb.append(",");
		sb.append("\"shields\":");
		subserialise(vessel.getShields());
		sb.append(",");
	}

	public void subserialise(Klingon klingon) {
		sb.append("\"type\":\"klingon\",\n");
		sb.append("\"disruptor\":");
		subserialise(klingon.getDisruptor());
		sb.append(",");
		sb.append("\"cloak\":");
		subserialise(klingon.getCloak());
		sb.append(",");
		sb.append("\"xp\":"+klingon.getXp()+",");
	}

	public void subserialise(Star star) {
		sb.append("\"type\":\"star\",");
	}

	public void subserialise(StarBase starbase) {
		sb.append("\"type\":\"starbase\",");
	}

	public void serialise(StarMap map, Quadrant quadrant) {
		sb.append("{");
		sb.append("\"name\":\"").append(quadrant.getName()).append("\",\n");
		sb.append("\"x\":").append(quadrant.x).append(",\n");
		sb.append("\"y\":").append(quadrant.y).append(",\n");
		sb.append("\"explored\":").append(quadrant.isExplored()).append(",\n");
		sb.append("\"things\":[");
		int length = sb.length();
		quadrant.doWithThings(thing->{
			serialise(thing);
			sb.append("\n,");
		});
		// length check tells us if doWithThings worked (it's a callback so it cannot modify state
		// variables outside the loop (have to be final).
		// the last "," is syntactically incorrect so we're removing it ONLY if the loop above ran.
		if (length<sb.length())
			sb.delete(sb.length()-1, sb.length());
		sb.append("]");
		closeScope("}");
	}
	
	public String serialise(StarMap map) {
		sb.append("{\"quadrants\":");
		sb.append("[");
		for (int y=0;y<Constants.SECTORS_EDGE;y++) 
		for (int x=0;x<Constants.SECTORS_EDGE;x++) {
				serialise(map, map.getQuadrant(x, y));
				if (!(x==Constants.SECTORS_EDGE-1 && x==y))
					sb.append(",");
			}
		sb.append("],");
		sb.append("\"stardate\":"+map.getStarDate()).append(",");
		sb.append("\"score\":"+app.gameController.getScoreKeeper().getScore());
		closeScope("}");
		return sb.toString();
	}
	
}
