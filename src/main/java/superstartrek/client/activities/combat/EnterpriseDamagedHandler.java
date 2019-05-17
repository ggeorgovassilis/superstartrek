package superstartrek.client.activities.combat;

import superstartrek.client.bus.BaseHandler;
import superstartrek.client.model.Enterprise;

public interface EnterpriseDamagedHandler extends BaseHandler{

	void onEnterpriseDamaged(Enterprise enterprise);
}
