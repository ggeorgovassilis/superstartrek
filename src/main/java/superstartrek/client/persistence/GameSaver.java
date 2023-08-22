package superstartrek.client.persistence;

import com.google.gwt.core.client.GWT;

import superstartrek.client.Application;
import superstartrek.client.eventbus.Events;
import superstartrek.client.space.StarMap;
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

	public boolean loadGame() {
		try {
			StarMapDeserialiser deserialiser = new StarMapDeserialiser(app);
			String json = app.browserAPI.getLocallyStoredValue("savegame");
			app.eventBus.fireEvent(Events.GAME_RESTART, (h) -> h.beforeGameRestart());
			StarMap starMap = deserialiser.readStarMap(json);
			app.starMap = starMap;
			app.eventBus.fireEvent(Events.GAME_STARTED, (h) -> h.onGameStarted(starMap));
			app.browserAPI.postHistoryChange("computer");
			return true;
		} catch (Exception e) {
			GWT.log(e.getMessage(), e);
			app.browserAPI.alert("Error while restoring game state. A new game will be started.");
			return false;
		}
	}

	public boolean doesSavedGameExist() {
		String json = Application.get().browserAPI.getLocallyStoredValue("savegame");
		return (!Strings.isEmpty(json));
	}
}
