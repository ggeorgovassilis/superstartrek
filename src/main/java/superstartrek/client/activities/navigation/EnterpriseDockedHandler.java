package superstartrek.client.activities.navigation;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.StarBase;

public interface EnterpriseDockedHandler extends EventHandler{

	public static class EnterpriseDockedEvent extends GwtEvent<EnterpriseDockedHandler>{

		public final static Type<EnterpriseDockedHandler> TYPE = new Type<>();
		public final Enterprise enterprise;
		public final StarBase starBase;
		
		public EnterpriseDockedEvent(Enterprise enterprise, StarBase starBase) {
			this.enterprise = enterprise;
			this.starBase = starBase;
		}
		
		@Override
		public Type<EnterpriseDockedHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(EnterpriseDockedHandler handler) {
			handler.onEnterpriseDocked(enterprise, starBase);
		}

	};

	void onEnterpriseDocked(Enterprise enterprise, StarBase starBase);
}
