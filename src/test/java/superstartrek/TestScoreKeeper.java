package superstartrek;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Map;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;

import superstartrek.client.control.ScoreKeeperImpl;

public class TestScoreKeeper extends BaseTest {

	ScoreKeeperImpl scoreKeeper;

	@Before
	public void setup() {
		scoreKeeper = new ScoreKeeperImpl(browser);
	}

	@Test
	public void testHighScoreList() {
		final String[] cache = new String[1];
		when(browser.getLocallyStoredValue(eq("scores"))).thenAnswer(t -> cache[0]);
		when(browser.storeValueLocally(eq("scores"), anyString())).thenAnswer(t -> {
			cache[0] = t.getArgument(1);
			return null;
		});

		when(browser.parseStringJsonArray(anyString())).thenAnswer((args) -> {
			String json = args.getArgument(0);
			JSONArray arr = new JSONArray(json);
			String s[] = new String[arr.length()];
			int i = 0;
			for (Object o : arr.toList()) {
				s[i++] = o.toString();
			}
			return s;
		});
		when(browser.toJson(any())).thenAnswer(invocation -> {
			String[] array = invocation.getArgument(0);
			JSONArray arr = new JSONArray();
			for (String s : array)
				arr.put(s);
			return arr.toString();
		});
		scoreKeeper.addScore(1);
		assertEquals(1, scoreKeeper.getScore());
		scoreKeeper.commitScore(new Date(100000l));
		Map<Date, Integer> highScores = scoreKeeper.getHighScores();
		assertEquals(1, highScores.size());
		assertEquals(1, (int) highScores.get(new Date(100000)));

		scoreKeeper.addScore(10);
		assertEquals(11, scoreKeeper.getScore());
		scoreKeeper.commitScore(new Date(200000l));
		highScores = scoreKeeper.getHighScores();
		assertEquals(2, highScores.size());
		assertEquals(1, (int) highScores.get(new Date(100000)));
		assertEquals(11, (int) highScores.get(new Date(200000)));

		scoreKeeper.addScore(20);
		assertEquals(31, scoreKeeper.getScore());
		scoreKeeper.commitScore(new Date(300000l));
		highScores = scoreKeeper.getHighScores();
		assertEquals(3, highScores.size());
		assertEquals(1, (int) highScores.get(new Date(100000)));
		assertEquals(11, (int) highScores.get(new Date(200000)));
		assertEquals(31, (int) highScores.get(new Date(300000)));

		scoreKeeper.addScore(30);
		assertEquals(61, scoreKeeper.getScore());
		scoreKeeper.commitScore(new Date(400000l));
		highScores = scoreKeeper.getHighScores();
		assertEquals(4, highScores.size());
		assertEquals(1, (int) highScores.get(new Date(100000)));
		assertEquals(11, (int) highScores.get(new Date(200000)));
		assertEquals(31, (int) highScores.get(new Date(300000)));
		assertEquals(61, (int) highScores.get(new Date(400000)));

		scoreKeeper.addScore(10);
		assertEquals(71, scoreKeeper.getScore());
		scoreKeeper.commitScore(new Date(500000l));
		highScores = scoreKeeper.getHighScores();
		assertEquals(5, highScores.size());
		assertEquals(1, (int) highScores.get(new Date(100000)));
		assertEquals(11, (int) highScores.get(new Date(200000)));
		assertEquals(31, (int) highScores.get(new Date(300000)));
		assertEquals(61, (int) highScores.get(new Date(400000)));
		assertEquals(71, (int) highScores.get(new Date(500000)));

		scoreKeeper.addScore(10);
		assertEquals(81, scoreKeeper.getScore());
		scoreKeeper.commitScore(new Date(600000l));
		highScores = scoreKeeper.getHighScores();
		assertEquals(5, highScores.size());
		assertEquals(11, (int) highScores.get(new Date(200000)));
		assertEquals(31, (int) highScores.get(new Date(300000)));
		assertEquals(61, (int) highScores.get(new Date(400000)));
		assertEquals(71, (int) highScores.get(new Date(500000)));
		assertEquals(81, (int) highScores.get(new Date(600000)));
}
}
