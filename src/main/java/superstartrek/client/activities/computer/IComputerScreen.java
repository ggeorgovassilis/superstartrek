package superstartrek.client.activities.computer;

import superstartrek.client.activities.View;

public interface IComputerScreen extends View<ComputerPresenter>{

	void updateShortStatus(String cssImpulse, String cssTactical, String cssPhasers, String cssTorpedos);

	void updateShields(int value, int currentUpperBound, int maximum);
	
	void updateAntimatter(int value, int maximum);
	
	void addAntimatterCss(String css);	

	void removeAntimatterCss(String css);	

	void showStarDate(String sd);

	void showScore(String score);

	void setRepairButtonCss(String css);
	
	void setRepairButtonEnabled(boolean enabled);
	
	void setQuadrantName(String name, String css);
	
	void enableLlrsButton();
	
	void disableLrsButton();

}