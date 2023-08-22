package superstartrek.client.activities.navigation;

import superstartrek.client.eventbus.EventHandler;
import superstartrek.client.vessels.Enterprise;

public interface EnterpriseRepairedHandler extends EventHandler{

	void onEnterpriseRepaired(Enterprise enterprise);
}
