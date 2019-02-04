package superstartrek.client.activities.loading;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;

public class GameOverEvent extends GwtEvent<GameOverHandler>{

	public enum Outcome{lost,won};
	final public static Type<GameOverHandler> TYPE = new Type<>();
	final Outcome outcome;
	
	public GameOverEvent(Outcome outcome) {
		this.outcome = outcome;
	}
	
	@Override
	public Type<GameOverHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(GameOverHandler handler) {
		if (outcome == Outcome.lost)
			handler.gameLost();
		if (outcome == Outcome.won)
			handler.gameWon();
		handler.gameOver();
	}

}
