package superstartrek.client.activities.computer;

import com.google.gwt.event.shared.GwtEvent;

import superstartrek.client.model.StarMap;

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
