package superstartrek.client.activities.klingons;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface KlingonDestroyedHandler extends EventHandler{

	public static class KlingonDestroyedEvent extends GwtEvent<KlingonDestroyedHandler>{

		public final static Type<KlingonDestroyedHandler> TYPE = new Type<KlingonDestroyedHandler>();
		protected final Klingon klingon;
		
		public KlingonDestroyedEvent(Klingon klingon) {
			this.klingon = klingon;
		}

		@Override
		public Type<KlingonDestroyedHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(KlingonDestroyedHandler handler) {
			handler.klingonDestroyed(klingon);
		}
	}

	void klingonDestroyed(Klingon klingon);
}
