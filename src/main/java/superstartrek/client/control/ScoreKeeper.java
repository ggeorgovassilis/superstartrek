package superstartrek.client.control;

public interface ScoreKeeper {
	
	final static int POINTS_KLINGON_RAIDER_DESTROYED = 100;
	final static int POINTS_KLINGON_BOF_DESTROYED = 300;
	final static int POINTS_ENTERPRISE_REPAIR = -20;
	final static int POINTS_DOCK_STARBASE = -50;
	final static int POINTS_DAY = -1;
	final static int POINTS_GAME_WON = 500;
	final static int POINTS_ENTERPRISE_DESTROYED = -500;

	int addScore(int score);
	int getScore();
}
