package superstartrek.client.control;

import com.google.gwt.event.shared.GwtEvent;

public class GameOverEvent extends GwtEvent<GamePhaseHandler>{

	public enum Outcome{lost,won};
	final public static Type<GamePhaseHandler> TYPE = new Type<>();
	final Outcome outcome;
	final String reason;
	
	public GameOverEvent(Outcome outcome, String reason) {
		this.outcome = outcome;
		this.reason = reason;
	}
	
	@Override
	public Type<GamePhaseHandler> getAssociatedType() {
		return TYPE;
	}
	
	public String getReason() {
		return reason;
	}

	@Override
	protected void dispatch(GamePhaseHandler handler) {
		if (outcome == Outcome.lost)
			handler.gameLost();
		if (outcome == Outcome.won)
			handler.gameWon();
		handler.gameOver();
	}

}
