package superstartrek.client.control;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface KeyPressedEventHandler extends EventHandler{

	public static class KeyPressedEvent extends GwtEvent<KeyPressedEventHandler>{

		public final static Type<KeyPressedEventHandler> TYPE = new Type<KeyPressedEventHandler>();
		public final int code;
		
		public KeyPressedEvent(int code) {
			this.code = code;
		}
		
		@Override
		public Type<KeyPressedEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(KeyPressedEventHandler handler) {
			handler.onKeyPressed(this);
		}
	}
	
	
	void onKeyPressed(KeyPressedEvent event);
}
