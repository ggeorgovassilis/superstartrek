package superstartrek.client.activities.navigation;

import com.google.gwt.event.shared.EventHandler;

import superstartrek.client.model.Enterprise;

public interface EnterpriseRepairedHandler extends EventHandler{

	void onEnterpriseRepaired(Enterprise enterprise);
}
