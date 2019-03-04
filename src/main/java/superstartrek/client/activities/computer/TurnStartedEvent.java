package superstartrek.client.activities.computer;

import com.google.gwt.event.shared.GwtEvent;

public class TurnStartedEvent extends GwtEvent<TurnStartedHandler>{

	public static Type<TurnStartedHandler> TYPE = new Type<TurnStartedHandler>();
	
	@Override
	public Type<TurnStartedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TurnStartedHandler handler) {
		handler.onTurnStarted(this);
	}

}
