package superstartrek.client.activities.navigation;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import superstartrek.client.model.Enterprise;

public interface EnterpriseDamagedHandler extends EventHandler{

	public static class EnterpriseDamagedEvent extends GwtEvent<EnterpriseDamagedHandler>{

		public final static Type<EnterpriseDamagedHandler> TYPE = new Type<>();
		public final Enterprise enterprise;
		
		public EnterpriseDamagedEvent(Enterprise enterprise) {
			this.enterprise = enterprise;
		}
		
		@Override
		public Type<EnterpriseDamagedHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(EnterpriseDamagedHandler handler) {
			handler.onEnterpriseDamaged(enterprise);
		}

	}

	void onEnterpriseDamaged(Enterprise enterprise);
}
