package superstartrek.client.activities.navigation;

import com.google.gwt.event.shared.GwtEvent;

import superstartrek.client.model.Enterprise;

public class EnterpriseRepairedEvent extends GwtEvent<EnterpriseRepairedHandler>{

	public final static Type<EnterpriseRepairedHandler> TYPE = new Type<>();
	public final Enterprise enterprise;
	
	public EnterpriseRepairedEvent(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	
	@Override
	public Type<EnterpriseRepairedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(EnterpriseRepairedHandler handler) {
		handler.onEnterpriseRepaired(enterprise);
	}

}
