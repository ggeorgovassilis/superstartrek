package superstartrek.client.control;

import com.google.gwt.event.shared.GwtEvent;

//TODO: nobody is listening to this event currently, but keeping for symmetry.
public class KlingonTurnEndedEvent extends GwtEvent<GamePhaseHandler> {

	public static Type<GamePhaseHandler> TYPE = new Type<GamePhaseHandler>();

	@Override
	public Type<GamePhaseHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(GamePhaseHandler handler) {
		handler.onKlingonTurnStarted();
	}

}
