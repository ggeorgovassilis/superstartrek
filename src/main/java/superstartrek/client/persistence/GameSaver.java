package superstartrek.client.persistence;

import superstartrek.client.Application;
import superstartrek.client.bus.Events;
import superstartrek.client.model.StarMap;
import superstartrek.client.utils.Strings;

public class GameSaver {
	
	Application app;
	
	public GameSaver(Application app) {
		this.app = app;
	}

	public void saveGame() {
		StarMapSerialiser sms = new StarMapSerialiser(app);
		String json = sms.serialise(app.starMap);
		app.browserAPI.storeValueLocally("savegame", json);
	}
	
	public void deleteGame() {
		app.browserAPI.deleteValueLocally("savegame");
	}
	
	public void loadGame() {
		StarMapDeserialiser deserialiser = new StarMapDeserialiser(app);
		String json = app.browserAPI.getLocallyStoredValue("savegame");
		app.eventBus.fireEvent(Events.GAME_RESTART, (h)->h.beforeGameRestart());
		StarMap starMap = deserialiser.readStarMap(json);
		app.starMap = starMap;
		app.eventBus.fireEvent(Events.GAME_STARTED, (h)->h.onGameStarted(starMap));
		app.browserAPI.postHistoryChange("computer");
		//app.eventBus.fireEvent(Events.TURN_STARTED, (h)->h.onTurnStarted());
	}
	
	public boolean doesSavedGameExist() {
		String json = Application.get().browserAPI.getLocallyStoredValue("savegame");
		return (!Strings.isEmpty(json));
	}
}
