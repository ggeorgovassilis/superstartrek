package superstartrek.client.control;

import com.google.gwt.event.shared.GwtEvent;

import superstartrek.client.model.StarMap;

public class GameStartedEvent extends GwtEvent<GamePhaseHandler>{
	
	public static Type<GamePhaseHandler> TYPE = new Type<GamePhaseHandler>();

	public final StarMap starMap;
	
	public GameStartedEvent(StarMap starMap) {
		this.starMap = starMap;
	}
	
	@Override
	public Type<GamePhaseHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(GamePhaseHandler handler) {
		handler.onGameStarted(this);
	}

}
