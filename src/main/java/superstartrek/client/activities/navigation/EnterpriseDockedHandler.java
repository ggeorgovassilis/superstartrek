package superstartrek.client.activities.navigation;

import superstartrek.client.bus.BaseHandler;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.StarBase;

public interface EnterpriseDockedHandler extends BaseHandler{

	void onEnterpriseDocked(Enterprise enterprise, StarBase starBase);
}
