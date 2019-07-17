package superstartrek.client.activities.navigation;

import superstartrek.client.bus.EventHandler;
import superstartrek.client.model.Enterprise;

public interface EnterpriseRepairedHandler extends EventHandler{

	void onEnterpriseRepaired(Enterprise enterprise, int itemsRepaired, int torpedosRestocked, int antimatterRefuelled);
}
