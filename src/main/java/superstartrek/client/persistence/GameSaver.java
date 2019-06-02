package superstartrek.client.persistence;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import superstartrek.client.Application;

public class GameSaver {

	public void saveGame(Application app) {
		StarMapSerialiser sms = new StarMapSerialiser();
		String json = sms.serialise(app.starMap);
		GWT.log(json);
		JSONValue v = JSONParser.parseStrict(json);
		GWT.log(v.toString());
	}
}
