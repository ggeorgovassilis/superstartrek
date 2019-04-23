package superstartrek.client.activities.appmenu;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface AppMenuHandler extends EventHandler {

	public static class AppMenuEvent extends GwtEvent<AppMenuHandler> {

		public final static Type<AppMenuHandler> TYPE = new Type<>();

		public enum Status {
			showMenu, hideMenu
		}

		public final Status status;

		public AppMenuEvent(Status status) {
			this.status = status;

		}

		@Override
		public Type<AppMenuHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(AppMenuHandler handler) {
			switch (status){
			case showMenu: handler.showMenu(); break;
			case hideMenu: handler.hideMenu(); break;
			}
		}
	}

	default void showMenu() {
	};

	default void hideMenu() {
	};
}
