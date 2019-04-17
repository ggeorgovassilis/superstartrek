package superstartrek.client.activities.computer;

import superstartrek.client.activities.IBaseView;
import superstartrek.client.model.Setting;

public interface IComputerView extends IBaseView<ComputerActivity>{

	void updateShortStatus(String cssImpulse, String cssTactical, String cssPhasers, String cssTorpedos);

	void updateShields(int value, int currentUpperBound, int maximum);

	void showStarDate(String sd);

	void setDockInStarbaseButtonVisibility(boolean visible);

	void setRepairButtonVisibility(boolean visible);
	
	void setQuadrantName(String name, String css);

}