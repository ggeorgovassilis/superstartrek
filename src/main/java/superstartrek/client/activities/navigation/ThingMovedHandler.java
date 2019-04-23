package superstartrek.client.activities.navigation;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.Thing;

public interface ThingMovedHandler extends EventHandler{

	public static class ThingMovedEvent extends GwtEvent<ThingMovedHandler>{

		public final static Type<ThingMovedHandler> TYPE = new Type<ThingMovedHandler>();
		
		public final Thing thing;
		public final Quadrant qFrom;
		public final Location lFrom;
		public final Quadrant qTo;
		public final Location lTo;
		
		public ThingMovedEvent(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo) {
			this.thing = thing;
			this.qFrom = qFrom;
			this.lFrom = lFrom;
			this.qTo = qTo;
			this.lTo = lTo;
		}
		
		@Override
		public Type<ThingMovedHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(ThingMovedHandler handler) {
			handler.thingMoved(thing, qFrom, lFrom, qTo, lTo);
		}

	}
	void thingMoved(Thing thing, Quadrant qFrom, Location lFrom, Quadrant qTo, Location lTo);
}
