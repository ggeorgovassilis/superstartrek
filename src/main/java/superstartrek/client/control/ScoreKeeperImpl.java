package superstartrek.client.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public void commitScore(Date date) {
        String newScoreEntry = String.format("%d,%d", date.getTime(), score);

        // Fetch existing scores from BrowserAPI
        String scoresJson = browserAPI.getLocallyStoredValue("scores");
        if (Strings.isEmpty(scoresJson))
        	scoresJson="[]";
        String[] scoresArr = browserAPI.parseStringJsonArray(scoresJson);

        List<String> scores = new ArrayList<String>();
        for (String entry:scoresArr)
        	scores.add(entry);
        scores.add(newScoreEntry);
        Collections.sort(scores);
        while (scores.size() > 5) {
            scores.remove(0); // Removes the lowest score
        }
        Collections.reverse(scores);
        
        scoresArr = new String[scores.size()];
        for (int i=0;i<scoresArr.length;i++)
        	scoresArr[i] = scores.get(i);
        String json = browserAPI.toJson(scoresArr);
        browserAPI.storeValueLocally("scores", json);
    }

	@Override
	public Map<Date, Integer> getHighScores() {
        Map<Date,Integer> map = new HashMap<Date, Integer>();
		String jsonScores = browserAPI.getLocallyStoredValue("scores");
        if (Strings.isEmpty(jsonScores))
        	jsonScores="[]";

        String[] scoresArr = browserAPI.parseStringJsonArray(jsonScores);
        for (String entry:scoresArr) {
        	String[] parts = entry.split(",");
        	long timestamp = Long.parseLong(parts[0]);
        	int score = Integer.parseInt(parts[1]);
        	Date date = new Date(timestamp);
        	map.put(date, score);
        }
        return map;
	}

}
