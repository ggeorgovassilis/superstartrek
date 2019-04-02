package superstartrek.client.activities.klingons;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface KlingonUncloakedHandler extends EventHandler{

	public class KlingonUncloakedEvent extends GwtEvent<KlingonUncloakedHandler>{

		public final static Type<KlingonUncloakedHandler> TYPE = new Type<KlingonUncloakedHandler>();
		protected final Klingon klingon;
		
		public KlingonUncloakedEvent(Klingon klingon) {
			this.klingon = klingon;
		}

		@Override
		public Type<KlingonUncloakedHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(KlingonUncloakedHandler handler) {
			handler.klingonUncloaked(klingon);
		}
	}

	void klingonUncloaked(Klingon klingon);
}
