package superstartrek.client.activities.navigation;

import superstartrek.client.bus.BaseHandler;
import superstartrek.client.model.Enterprise;

public interface EnterpriseRepairedHandler extends BaseHandler{

	void onEnterpriseRepaired(Enterprise enterprise);
}
