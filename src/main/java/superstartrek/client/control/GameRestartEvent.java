package superstartrek.client.control;

import com.google.gwt.event.shared.GwtEvent;

public class GameRestartEvent extends GwtEvent<GamePhaseHandler>{

	public final static Type<GamePhaseHandler> TYPE = new Type<>();
	
	@Override
	public Type<GamePhaseHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(GamePhaseHandler handler) {
		handler.beforeGameRestart();
	}

}
