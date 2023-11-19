package superstartrek;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import org.junit.Before;
import org.junit.Test;

import superstartrek.client.activities.highscores.HighscoresPresenter;
import superstartrek.client.activities.highscores.HighscoresScreen;
import superstartrek.client.control.ScoreKeeperImpl;

public class TestScoreKeeper extends BaseTest {

	ScoreKeeperImpl scoreKeeper;

	@Before
	public void setup() {
		scoreKeeper = new ScoreKeeperImpl(browser);
		application.scoreKeeper = scoreKeeper;
	}

	@Test
	public void testScoreKeeper() {
		assertEquals(0, scoreKeeper.getScore());
		scoreKeeper.addScore(10);
		assertEquals(10, scoreKeeper.getScore());
		scoreKeeper.addScore(1);
		assertEquals(11, scoreKeeper.getScore());

		when(browser.getLocallyStoredValue("scores")).thenReturn(null, "11_1000", "11_1000", "11_1000,12_1001",
				"11_1000,12_1001", "11_1000,12_1001,13_1002", "11_1000,12_1001,13_1002",
				"11_1000,12_1001,13_1002,14_1003", "11_1000,12_1001,13_1002,14_1003",
				"11_1000,12_1001,13_1002,14_1003,15_1004", "11_1000,12_1001,13_1002,14_1003,15_1004", "12_1001,13_1002,14_1003,15_1004,16_1005");
		scoreKeeper.commitScore(new Date(1000));
		String[] scores = scoreKeeper.getHighScores();
		assertEquals(1, scores.length);
		assertEquals("11_1000", scores[0]);

		scoreKeeper.addScore(1);
		scoreKeeper.commitScore(new Date(1001));
		scores = scoreKeeper.getHighScores();
		assertEquals(2, scores.length);
		assertEquals("11_1000", scores[0]);
		assertEquals("12_1001", scores[1]);

		scoreKeeper.addScore(1);
		scoreKeeper.commitScore(new Date(1002));
		scores = scoreKeeper.getHighScores();
		assertEquals(3, scores.length);
		assertEquals("11_1000", scores[0]);
		assertEquals("12_1001", scores[1]);
		assertEquals("13_1002", scores[2]);

		scoreKeeper.addScore(1);
		scoreKeeper.commitScore(new Date(1003));
		scores = scoreKeeper.getHighScores();
		assertEquals(4, scores.length);
		assertEquals("11_1000", scores[0]);
		assertEquals("12_1001", scores[1]);
		assertEquals("13_1002", scores[2]);
		assertEquals("14_1003", scores[3]);

		scoreKeeper.addScore(1);
		scoreKeeper.commitScore(new Date(1004));
		scores = scoreKeeper.getHighScores();
		assertEquals(5, scores.length);
		assertEquals("11_1000", scores[0]);
		assertEquals("12_1001", scores[1]);
		assertEquals("13_1002", scores[2]);
		assertEquals("14_1003", scores[3]);
		assertEquals("15_1004", scores[4]);

		scoreKeeper.addScore(1);
		scoreKeeper.commitScore(new Date(1005));
		scores = scoreKeeper.getHighScores();
		assertEquals(5, scores.length);
		assertEquals("12_1001", scores[0]);
		assertEquals("13_1002", scores[1]);
		assertEquals("14_1003", scores[2]);
		assertEquals("15_1004", scores[3]);
		assertEquals("16_1005", scores[4]);
	}

	@Test
	public void testHighScoreList() {
		HighscoresScreen screen = mock(HighscoresScreen.class);
		HighscoresPresenter presenter = new HighscoresPresenter();
		presenter.setView(screen);

		when(browser.getLocallyStoredValue("scores")).thenReturn("12_1001000,13_1002000,14_1003000,15_1004000,16_1005000");

		presenter.onActivityChanged("highscore");
		verify(screen).clearEntries();
		verify(screen).addEntry("1970/01/01 02:16:41", "12");
		verify(screen).addEntry("1970/01/01 02:16:42", "13");
		verify(screen).addEntry("1970/01/01 02:16:43", "14");
		verify(screen).addEntry("1970/01/01 02:16:44", "15");
		verify(screen).addEntry("1970/01/01 02:16:45", "16");
}
}
