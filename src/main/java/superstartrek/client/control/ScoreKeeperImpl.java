package superstartrek.client.control;

public class ScoreKeeperImpl implements ScoreKeeper{

	int score = 0;
	
	@Override
	public int addScore(int score) {
		this.score+=score;
		return this.score;
	}

	@Override
	public int getScore() {
		return score;
	}

}
