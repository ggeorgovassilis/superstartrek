package superstartrek.client.activities.sector.contextmenu;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import superstartrek.client.model.Location;
import superstartrek.client.model.Quadrant;

public interface ContextMenuHideHandler extends EventHandler{
	public static class ContextMenuHideEvent extends GwtEvent<ContextMenuHideHandler>{

		public static Type<ContextMenuHideHandler> TYPE = new Type<ContextMenuHideHandler>();

		@Override
		public Type<ContextMenuHideHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(ContextMenuHideHandler handler) {
			handler.onMenuHide();
		}
		
	}
	
	void onMenuHide();
}
