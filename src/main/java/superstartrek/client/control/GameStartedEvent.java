package superstartrek.client.control;

import com.google.gwt.event.shared.GwtEvent;

public class GameStartedEvent extends GwtEvent<GamePhaseHandler>{

	public static Type<GamePhaseHandler> TYPE = new Type<GamePhaseHandler>();

	@Override
	public Type<GamePhaseHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(GamePhaseHandler handler) {
		handler.onGameStarted(this);
	}

}
