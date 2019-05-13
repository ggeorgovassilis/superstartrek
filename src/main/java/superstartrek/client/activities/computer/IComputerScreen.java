package superstartrek.client.activities.computer;

import superstartrek.client.activities.View;

public interface IComputerScreen extends View{

	void updateShortStatus(String cssImpulse, String cssTactical, String cssPhasers, String cssTorpedos);

	void updateShields(int value, int currentUpperBound, int maximum);
	
	void updateAntimatter(int value, int maximum);

	void showStarDate(String sd);

	void showScore(String score);

	void setRepairButtonLabel(String label);
	
	void setRepairButtonEnabled(boolean enabled);
	
	void setQuadrantName(String name, String css);
	
	void enableLlrsButton();
	
	void disableLrsButton();

}