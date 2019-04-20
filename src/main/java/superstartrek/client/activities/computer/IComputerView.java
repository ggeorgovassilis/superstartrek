package superstartrek.client.activities.computer;

import superstartrek.client.activities.IBaseView;

public interface IComputerView extends IBaseView<ComputerActivity>{

	void updateShortStatus(String cssImpulse, String cssTactical, String cssPhasers, String cssTorpedos);

	void updateShields(int value, int currentUpperBound, int maximum);
	
	void updateAntimatter(int value, int maximum);

	void showStarDate(String sd);

	void setDockInStarbaseButtonVisibility(boolean visible);

	void setRepairButtonVisibility(boolean visible);
	
	void setQuadrantName(String name, String css);

}