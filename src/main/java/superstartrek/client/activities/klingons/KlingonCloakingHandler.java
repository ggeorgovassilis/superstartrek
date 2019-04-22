package superstartrek.client.activities.klingons;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface KlingonCloakingHandler extends EventHandler{

	public static class KlingonUncloakedEvent extends GwtEvent<KlingonCloakingHandler>{

		public final static Type<KlingonCloakingHandler> TYPE = new Type<KlingonCloakingHandler>();
		protected final Klingon klingon;
		
		public KlingonUncloakedEvent(Klingon klingon) {
			this.klingon = klingon;
		}

		@Override
		public Type<KlingonCloakingHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(KlingonCloakingHandler handler) {
			handler.klingonUncloaked(klingon);
		}
	}

	public static class KlingonCloakedEvent extends GwtEvent<KlingonCloakingHandler>{

		public final static Type<KlingonCloakingHandler> TYPE = new Type<KlingonCloakingHandler>();
		protected final Klingon klingon;
		
		public KlingonCloakedEvent(Klingon klingon) {
			this.klingon = klingon;
		}

		@Override
		public Type<KlingonCloakingHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(KlingonCloakingHandler handler) {
			handler.klingonCloaked(klingon);
		}
	}

	default void klingonUncloaked(Klingon klingon) {};
	default void klingonCloaked(Klingon klingon) {};
}
