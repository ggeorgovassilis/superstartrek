package superstartrek.client.activities.computer;

import com.google.gwt.event.shared.GwtEvent;

public class TurnEndedEvent extends GwtEvent<TurnEndedHandler>{

	public static Type<TurnEndedHandler> TYPE = new Type<TurnEndedHandler>();

	@Override
	public Type<TurnEndedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TurnEndedHandler handler) {
		handler.onTurnEnded(this);
	}

}
