package superstartrek.client.activities.navigation;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import superstartrek.client.model.Enterprise;

public interface EnterpriseRepairedHandler extends EventHandler{

	public static class EnterpriseRepairedEvent extends GwtEvent<EnterpriseRepairedHandler>{

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

	void onEnterpriseRepaired(Enterprise enterprise);
}
