package superstartrek.client.activities.highscores;

import superstartrek.client.activities.View;

public interface HighscoresScreen extends View<HighscoresPresenter>{

	void clearEntries();
	void addEntry(String date, String score);
}
