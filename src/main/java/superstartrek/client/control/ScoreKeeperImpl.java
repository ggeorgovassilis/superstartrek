package superstartrek.client.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import superstartrek.client.space.Constants;
import superstartrek.client.utils.BrowserAPI;
import superstartrek.client.utils.Strings;

public class ScoreKeeperImpl implements ScoreKeeper {

	int score = 0;
	BrowserAPI browserAPI;

	public ScoreKeeperImpl(BrowserAPI browserAPI) {
		this.browserAPI = browserAPI;
	}

	@Override
	public int addScore(int score) {
		this.score += score;
		return this.score;
	}

	@Override
	public int getScore() {
		return score;
	}

	@Override
	public Void reset() {
		score = 0;
		return null;
	}

	String toString(int score, Date when) {
		return score + "_" + when.getTime();
	}

	String[] parseScoresString(String s) {
		String[] arr = Strings.isEmpty(s)?new String[0]:s.split(",");
		return arr;
	}
	
	String getStoredScore() {
		return Strings.denull(browserAPI.getLocallyStoredValue("scores"));
	}

	@Override
	public void commitScore(Date date) {
		String scoresString = getStoredScore();
		String[] scoresArr = parseScoresString(scoresString);
		List<String> lScores = new ArrayList<String>(Arrays.asList(scoresArr));
		String newScoreEntry = toString(score, date);
		lScores.add(newScoreEntry);
		Collections.sort(lScores);
		if (lScores.size() > Constants.MAX_HIGH_SCORE_ENTRIES)
			lScores.remove(0);
		scoresArr = lScores.toArray(scoresArr);
		scoresString = String.join(",", scoresArr);
		browserAPI.storeValueLocally("scores", scoresString);
	}

	@Override
	public String[] getHighScores() {
		String scoresString = getStoredScore();
		return parseScoresString(scoresString);
	}

}
