package superstartrek.client.activities.highscores;

import static superstartrek.client.eventbus.Events.ACTIVITY_CHANGED;

import java.util.Arrays;
import java.util.Date;

import superstartrek.client.activities.ActivityChangedHandler;
import superstartrek.client.activities.BasePresenter;

public class HighscoresPresenter extends BasePresenter<HighscoresScreen> implements ActivityChangedHandler{

	public HighscoresPresenter() {
		addHandler(ACTIVITY_CHANGED);
	}
	
	String pad(int i) {
		return ((i<10)?"0":"")+i;
	}
	
	@SuppressWarnings("deprecation")
	String format(Date d) {
		return (1900 + d.getYear()) + "/" + pad((1 + d.getMonth())) + "/" + pad((d.getDate())) + " "
				+ pad(d.getHours()) + ":" + pad(d.getMinutes())+":"+pad(d.getSeconds());
	}
	
	void updateHighScoreList() {
		HighscoresScreen screen = (HighscoresScreen)view;
		screen.clearEntries();
		String[] scores = getApplication().scoreKeeper.getHighScores();
		Arrays.sort(scores);
		for (String entry:scores) {
			String[] parts = entry.split("_");
			Date date = new Date(Long.parseLong(parts[1]));
			String sDate = format(date);
			String sScore = parts[0];
			screen.addEntry(sDate, sScore);
		}
	}
	
	@Override
	public void onActivityChanged(String activity) {
		if ("highscore".equals(activity)) {
			updateHighScoreList();
			view.show();
		} else {
			view.hide();
		}
	}

}
