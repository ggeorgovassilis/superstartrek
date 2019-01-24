package superstartrek.client.activities.computer;

import com.google.gwt.event.shared.GwtEvent;

public class ComputerEvent extends GwtEvent<ComputerHandler> {
	
	public enum Action{hideScreen,showScreen};
	
	protected final Action action;

	public static Type<ComputerHandler> TYPE = new Type<ComputerHandler>();

	public ComputerEvent(Action action) {
		this.action = action;
	}
	
	@Override
	public Type<ComputerHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ComputerHandler handler) {
		if (action == Action.hideScreen)
			handler.hideScreen();
		else
		if (action == Action.showScreen)
			handler.showScreen();
	}

}
