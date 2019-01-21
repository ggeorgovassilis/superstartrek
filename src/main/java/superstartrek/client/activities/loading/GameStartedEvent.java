package superstartrek.client.activities.loading;

import com.google.gwt.event.shared.GwtEvent;

public class GameStartedEvent extends GwtEvent<GameStartedHandler>{

	public static Type<GameStartedHandler> TYPE = new Type<GameStartedHandler>();

	@Override
	public Type<GameStartedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(GameStartedHandler handler) {
		handler.onGameStared(this);
	}

}
