package superstartrek.client.persistence;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.storage.client.Storage;

import superstartrek.client.Application;
import superstartrek.client.bus.Events;
import superstartrek.client.model.StarMap;

public class GameSaver {
	

	public void saveGame(Application app) {
		StarMapSerialiser sms = new StarMapSerialiser();
		String json = sms.serialise(app.starMap);
		GWT.log(json);
		JSONValue v = JSONParser.parseStrict(json);
		GWT.log(v.toString());
		Storage.getLocalStorageIfSupported().setItem("savegame", json);
	}
	
	public void loadGame(Application app) {
		StarMapDeserialiser deserialiser = new StarMapDeserialiser(app);
		String json = Storage.getLocalStorageIfSupported().getItem("savegame");
		GWT.log(json);
		app.eventBus.fireEvent(Events.GAME_RESTART, (h)->h.beforeGameRestart());
		StarMap starMap = deserialiser.readStarMap(json);
		app.starMap = starMap;
		app.eventBus.fireEvent(Events.GAME_STARTED, (h)->h.onGameStarted(starMap));
		app.browserAPI.postHistoryChange("computer");
	}
}
