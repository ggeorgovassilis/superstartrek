package superstartrek.client.activities.computer;

import superstartrek.client.activities.View;

public interface IComputerScreen extends View<ComputerPresenter>{

	void updateShortStatus(String cssImpulse, String cssTactical, String cssPhasers, String cssTorpedos);

	void updateShields(double value, double currentUpperBound, double maximum);
	
	void updateAntimatter(double value, double maximum);
	
	void addAntimatterCss(String css);	

	void removeAntimatterCss(String css);	

	void showStarDate(String sd);

	void showScore(String score);

	void setRepairButtonCss(String css);
	
	void setRepairButtonEnabled(boolean enabled);
	
	void setQuadrantName(String name, String css);
	
	void enableLlrsButton();
	
	void disableLrsButton();

	void setCommandBarMode(String mode);
	
	void setScanProperty(String rowId, String cellId, String rowCss, String value);
}