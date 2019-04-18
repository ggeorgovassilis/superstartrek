package superstartrek.client.activities.computer;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;

public interface ComputerHandler extends EventHandler{

	public static class ComputerEvent extends GwtEvent<ComputerHandler> {
		
		public static Type<ComputerHandler> TYPE = new Type<ComputerHandler>();

		public enum Action{hideScreen,showScreen};
		
		protected final Action action;


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
	void showScreen();
	void hideScreen();
}
