package superstartrek.client.activities.navigation;

import com.google.gwt.event.shared.GwtEvent;

public class EnterpriseRepairedEvent extends GwtEvent<EnterpriseRepairedHandler>{

	public final static Type<EnterpriseRepairedHandler> TYPE = new Type<>();
	@Override
	public Type<EnterpriseRepairedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(EnterpriseRepairedHandler handler) {
		handler.onEnterpriseRepaired();
	}

}
